package net.alex9849.cocktailmaker.service.pumps;

import net.alex9849.cocktailmaker.model.FeasibilityReport;
import net.alex9849.cocktailmaker.model.cocktail.CocktailProgress;
import net.alex9849.cocktailmaker.model.eventaction.EventTrigger;
import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.model.recipe.CocktailOrderConfiguration;
import net.alex9849.cocktailmaker.model.recipe.FeasibilityFactory;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.IngredientGroup;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.cocktail.CocktailOrderConfigurationDto;
import net.alex9849.cocktailmaker.payload.dto.cocktail.FeasibilityReportDto;
import net.alex9849.cocktailmaker.service.EventService;
import net.alex9849.cocktailmaker.service.IngredientService;
import net.alex9849.cocktailmaker.service.WebSocketService;
import net.alex9849.cocktailmaker.service.pumps.cocktailfactory.CocktailFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class CocktailOrderService {
    private CocktailFactory cocktailFactory;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private PumpDataService pumpDataService;

    @Autowired
    private PumpMaintenanceService pumpUpService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private EventService eventService;


    public synchronized void orderCocktail(User user, Recipe recipe, CocktailOrderConfiguration orderConfiguration) {
        if(this.cocktailFactory != null) {
            throw new IllegalArgumentException("A cocktail is already being fabricated!");
        }
        FeasibilityFactory feasibilityFactory = this.checkFeasibility(recipe, orderConfiguration);
        FeasibilityReport report = feasibilityFactory.getFeasibilityReport();
        if(report.getRequiredIngredients().stream().anyMatch(x -> x.getAmountMissing() > 0)) {
            throw new IllegalArgumentException("Some pumps don't have enough liquids left!");
        }
        if(!report.isFeasible()) {
            throw new IllegalArgumentException("Cocktail not feasible!");
        }
        CocktailFactory cocktailFactory = new CocktailFactory(feasibilityFactory.getFeasibleRecipe(), user,
                new HashSet<>(pumpDataService.getAllPumps().stream().filter(x -> x instanceof DcPump).map(x -> (DcPump) x).collect(Collectors.toList())), p -> p.forEach(x -> pumpDataService.updatePump(x)))
                .subscribeProgress(this::onCocktailProgressSubscriptionChange);
        this.cocktailFactory = cocktailFactory;
        this.cocktailFactory.makeCocktail();
        this.pumpUpService.reschedulePumpBack();
    }

    private void onCocktailProgressSubscriptionChange(CocktailProgress progress) {
        if(progress.getState() == CocktailProgress.State.CANCELLED || progress.getState() == CocktailProgress.State.FINISHED) {
            this.scheduler.schedule(() -> {
                this.cocktailFactory = null;
                this.webSocketService.broadcastCurrentCocktailProgress(null);
                this.pumpUpService.reschedulePumpBack();
            }, 5000, TimeUnit.MILLISECONDS);
            this.webSocketService.broadcastPumpLayout(pumpDataService.getAllPumps());
        }
        this.webSocketService.broadcastCurrentCocktailProgress(progress);

        switch (progress.getState()) {
            case RUNNING:
                if (progress.getPreviousState() == CocktailProgress.State.READY_TO_START) {
                    eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_STARTED);
                }
                break;
            case MANUAL_ACTION_REQUIRED:
            case MANUAL_INGREDIENT_ADD:
                eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_MANUAL_INTERACTION_REQUESTED);
                break;
            case CANCELLED:
                eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_CANCELED);
                break;
            case FINISHED:
                eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_FINISHED);
                break;
        }
    }

    public FeasibilityFactory checkFeasibility(Recipe recipe, CocktailOrderConfiguration orderConfig) {
        return new FeasibilityFactory(recipe, orderConfig, pumpDataService.getAllPumps());
    }

    public synchronized void continueCocktailProduction() {
        if (this.cocktailFactory == null || this.cocktailFactory.isFinished()) {
            throw new IllegalStateException("No cocktail is being prepared currently!");
        }
        this.cocktailFactory.continueProduction();
    }

    public synchronized boolean isMakingCocktail() {
        return this.cocktailFactory != null
                && !this.cocktailFactory.isCanceled()
                && !this.cocktailFactory.isFinished();
    }

    public synchronized boolean cancelCocktailOrder() {
        if(this.cocktailFactory == null || this.cocktailFactory.isFinished()) {
            return false;
        }
        this.cocktailFactory.cancelCocktail();
        return true;
    }

    public CocktailProgress getCurrentCocktailProgress() {
        if(this.cocktailFactory == null) {
            return null;
        }
        return this.cocktailFactory.getCocktailprogress();
    }

    public CocktailOrderConfiguration fromDto(CocktailOrderConfigurationDto.Request.Create orderConfigDto) {
        if(orderConfigDto == null) {
            return null;
        }
        CocktailOrderConfiguration orderConfig = new CocktailOrderConfiguration();
        BeanUtils.copyProperties(orderConfigDto, orderConfig);
        long pStep = 0;
        Map<Long, Map<Long, AddableIngredient>> replacements = new HashMap<>();
        orderConfig.setProductionStepReplacements(replacements);
        for(List<FeasibilityReportDto.IngredientGroupReplacementDto.Request.Create> pStepDto : orderConfigDto.getProductionStepReplacements()) {
            Map<Long, AddableIngredient> stepReplacements = replacements.computeIfAbsent(pStep, k -> new HashMap<>());
            for(FeasibilityReportDto.IngredientGroupReplacementDto.Request.Create replacementDto : pStepDto) {
                Ingredient toReplace = ingredientService.getIngredient(replacementDto.getIngredientGroupId());
                if(toReplace == null) {
                    throw new IllegalArgumentException("IngredientGroup with id \"" + replacementDto.getIngredientGroupId() + "\" not found!");
                }
                if(!(toReplace instanceof IngredientGroup)) {
                    throw new IllegalArgumentException("Ingredient to replace with id \"" + toReplace.getName() + "\" is not a IngredientGroup!");
                }
                AddableIngredient addableIngredientReplacement = null;
                if(replacementDto.getReplacementId() != null) {
                    Ingredient replacement = ingredientService.getIngredient(replacementDto.getReplacementId());
                    if(replacement == null) {
                        throw new IllegalArgumentException("AddableIngredient with id \"" + replacementDto.getReplacementId() + "\" not found!");
                    }
                    if(!(replacement instanceof AddableIngredient)) {
                        throw new IllegalArgumentException("Replacement-Ingredient with id \"" + replacement.getName() + "\" is not an AddableIngredient!");
                    }
                    addableIngredientReplacement = (AddableIngredient) replacement;
                }
                stepReplacements.put(toReplace.getId(), addableIngredientReplacement);
            }
            pStep++;
        }

        CocktailOrderConfigurationDto.CustomisationsDto.Request.Create customisationsDto = orderConfigDto.getCustomisations();
        CocktailOrderConfiguration.Customisations customisations = new CocktailOrderConfiguration.Customisations();
        customisations.setBoost(customisationsDto.getBoost());

        List<CocktailOrderConfiguration.Customisations.AdditionalIngredient> additionalIngredients = new ArrayList<>();
        for(CocktailOrderConfigurationDto.CustomisationsDto.AdditionalIngredientDto.Request.Create additionalIngredientDto : customisationsDto.getAdditionalIngredients()) {
            CocktailOrderConfiguration.Customisations.AdditionalIngredient additionalIngredient = new CocktailOrderConfiguration.Customisations.AdditionalIngredient();
            additionalIngredient.setAmount(additionalIngredientDto.getAmount());
            Ingredient ingredient = ingredientService.getIngredient(additionalIngredientDto.getIngredientId());

            if(ingredient == null) {
                throw new IllegalArgumentException("AddableIngredient with id \"" + additionalIngredientDto.getIngredientId() + "\" not found!");
            }
            if(!(ingredient instanceof AddableIngredient)) {
                throw new IllegalArgumentException("Ingredient with id \"" + ingredient.getName() + "\" is not an AddableIngredient!");
            }
            AddableIngredient addableIngredient = (AddableIngredient) ingredient;

            additionalIngredient.setIngredient(addableIngredient);
            additionalIngredients.add(additionalIngredient);
        }
        customisations.setAdditionalIngredients(additionalIngredients);

        orderConfig.setCustomisations(customisations);

        return orderConfig;
    }

}
