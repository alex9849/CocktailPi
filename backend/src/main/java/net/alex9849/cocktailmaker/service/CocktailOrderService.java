package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.FeasibilityReport;
import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.cocktail.CocktailProgress;
import net.alex9849.cocktailmaker.model.eventaction.EventTrigger;
import net.alex9849.cocktailmaker.model.recipe.CocktailOrderConfiguration;
import net.alex9849.cocktailmaker.model.recipe.FeasibilityFactory;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.IngredientGroup;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.cocktail.CocktailOrderConfigurationDto;
import net.alex9849.cocktailmaker.payload.dto.cocktail.FeasibilityReportDto;
import net.alex9849.cocktailmaker.service.cocktailfactory.CocktailFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class CocktailOrderService {
    private CocktailFactory cocktailFactory;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private PumpService pumpService;

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
        List<Pump> pumps = pumpService.getAllPumps();
        if(pumps.stream().anyMatch(p -> this.pumpService.getPumpOccupation(p) != PumpService.PumpOccupation.NONE)) {
            throw new IllegalStateException("Some pumps are occupied currently!");
        }
        FeasibilityFactory feasibilityFactory = this.checkFeasibility(recipe, orderConfiguration);
        FeasibilityReport report = feasibilityFactory.getFeasibilityReport();
        if(!report.getInsufficientIngredients().isEmpty()) {
            throw new IllegalArgumentException("Some pumps don't have enough liquids left!");
        }
        if(!report.isFeasible()) {
            throw new IllegalArgumentException("Cocktail not feasible!");
        }
        this.cocktailFactory = new CocktailFactory(feasibilityFactory.getFeasibleRecipe(), user, new HashSet<>(pumpService.getAllPumps()))
                .subscribeProgress(this::onCocktailProgressSubscriptionChange);
        for(Pump pump : this.cocktailFactory.getUpdatedPumps()) {
            pumpService.updatePump(pump);
        }
        webSocketService.broadcastPumpLayout(pumpService.getAllPumps());
        this.cocktailFactory.makeCocktail();
    }

    private void onCocktailProgressSubscriptionChange(CocktailProgress progress) {
        if(progress.getState() == CocktailProgress.State.CANCELLED || progress.getState() == CocktailProgress.State.FINISHED) {
            this.scheduler.schedule(() -> {
                this.cocktailFactory = null;
                this.webSocketService.broadcastCurrentCocktailProgress(null);
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
                eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_CANCELED);
                break;
            case FINISHED:
                eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_FINISHED);
                break;
        }
    }

    public FeasibilityFactory checkFeasibility(Recipe recipe, CocktailOrderConfiguration orderConfig) {
        CocktailFactory.transformToAmountOfLiquid(recipe, orderConfig.getAmountOrderedInMl());
        return new FeasibilityFactory(recipe, orderConfig, pumpService.getAllPumps());
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
        Map<Pump, Integer> notUsedLiquidByPump = this.cocktailFactory.getNotUsedLiquid();
        for(Map.Entry<Pump, Integer> entry : notUsedLiquidByPump.entrySet()) {
            Pump pump = entry.getKey();
            Integer notUsedLiquid = entry.getValue();
            pump.setFillingLevelInMl(pump.getFillingLevelInMl() + notUsedLiquid);
            this.pumpService.updatePump(pump);
        }
        webSocketService.broadcastPumpLayout(pumpService.getAllPumps());
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
        return orderConfig;
    }

}