package net.alex9849.cocktailpi.service.pumps;

import net.alex9849.cocktailpi.model.FeasibilityReport;
import net.alex9849.cocktailpi.model.cocktail.CocktailProgress;
import net.alex9849.cocktailpi.model.eventaction.EventTrigger;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.recipe.CocktailOrderConfiguration;
import net.alex9849.cocktailpi.model.recipe.FeasibilityFactory;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.IngredientGroup;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.cocktail.CocktailOrderConfigurationDto;
import net.alex9849.cocktailpi.payload.dto.cocktail.FeasibilityReportDto;
import net.alex9849.cocktailpi.payload.dto.system.settings.PowerLimitSettingsDto;
import net.alex9849.cocktailpi.service.EventService;
import net.alex9849.cocktailpi.service.IngredientService;
import net.alex9849.cocktailpi.service.PowerLimitService;
import net.alex9849.cocktailpi.service.WebSocketService;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.CocktailFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class CocktailOrderService {
    private CocktailFactory cocktailFactory;
    private CocktailProgress prevCocktailProgress;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private PumpDataService pumpDataService;

    @Autowired
    private PumpMaintenanceService pumpUpService;

    @Autowired
    private PowerLimitService powerLimitService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private EventService eventService;


    public synchronized void orderCocktail(User user, Recipe recipe, CocktailOrderConfiguration orderConfiguration, Runnable onFinishCallback) {
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
        PowerLimitSettingsDto.Duplex.Detailed powerLimitSettings = powerLimitService.getPowerLimit();
        Integer powerLimit = null;
        if(powerLimitSettings.isEnable()) {
            powerLimit = powerLimitSettings.getLimit();
        }
        CocktailFactory cocktailFactory = new CocktailFactory(feasibilityFactory.getFeasibleRecipe(), user,
                new HashSet<>(pumpDataService.getAllCompletedPumps()), powerLimit, this::onRequestPumpPersist)
                .subscribeProgress(this::onCocktailProgressSubscriptionChange)
                .subscribeProgress(progess -> {
                    switch (progess.getState()) {
                        case FINISHED, CANCELLED, ERROR -> onFinishCallback.run();
                    }
                });
        this.cocktailFactory = cocktailFactory;
        this.cocktailFactory.makeCocktail();
        this.pumpUpService.reschedulePumpBack();
    }

    private void onRequestPumpPersist(Set<Pump> pumps) {
        pumps.forEach(p -> pumpDataService.updatePump(p));
        webSocketService.broadcastPumpLayout(pumpDataService.getAllPumps());
    }

    private void onCocktailProgressSubscriptionChange(CocktailProgress progress) {
        if(progress.getState() == CocktailProgress.State.CANCELLED
                || progress.getState() == CocktailProgress.State.ERROR
                || progress.getState() == CocktailProgress.State.FINISHED) {
            this.scheduler.schedule(() -> {
                this.cocktailFactory = null;
                this.prevCocktailProgress = null;
                this.webSocketService.broadcastCurrentCocktailProgress(null);
                this.pumpUpService.reschedulePumpBack();
            }, 5000, TimeUnit.MILLISECONDS);
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
            case ERROR:
                eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_CANCELED);
                break;
            case FINISHED:
                eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_FINISHED);
                break;
        }
        if(prevCocktailProgress != null) {
            if(prevCocktailProgress.getState() == CocktailProgress.State.MANUAL_ACTION_REQUIRED
                    || prevCocktailProgress.getState() == CocktailProgress.State.MANUAL_INGREDIENT_ADD) {
                if(progress.getState() == CocktailProgress.State.RUNNING) {
                    eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_MANUAL_INTERACTION_COMPLETED);
                }
            }
        }
        prevCocktailProgress = progress;
    }

    public FeasibilityFactory checkFeasibility(Recipe recipe, CocktailOrderConfiguration orderConfig) {
        return new FeasibilityFactory(recipe, orderConfig, pumpDataService.getAllCompletedPumps());
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
        this.cocktailFactory.cancelCocktail(false);
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
        Map<Long, AddableIngredient> replacements = new HashMap<>();
        orderConfig.setProductionStepReplacements(replacements);
        for(FeasibilityReportDto.IngredientGroupReplacementDto.Request.Create replacementDto : orderConfigDto.getIngredientGroupReplacements()) {
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
            replacements.put(toReplace.getId(), addableIngredientReplacement);
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
