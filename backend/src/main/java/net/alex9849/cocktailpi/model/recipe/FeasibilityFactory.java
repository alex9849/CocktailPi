package net.alex9849.cocktailpi.model.recipe;

import net.alex9849.cocktailpi.model.FeasibilityReport;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.IngredientGroup;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.service.GlassService;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.CocktailFactory;
import net.alex9849.cocktailpi.utils.SpringUtility;

import java.util.*;
import java.util.stream.Collectors;

public class FeasibilityFactory {
    private final Recipe recipe;
    private final CocktailOrderConfiguration orderConfiguration;
    private final List<Pump> pumps;
    private final FeasibilityReport feasibilityReport;
    private final FeasibleRecipe feasibleRecipe;

    /**
     * @param recipe The recipe that should be checked. Note: IngredientGroups within this instance will be replaced. Don't continue to use this object
     */
    public FeasibilityFactory(Recipe recipe, CocktailOrderConfiguration orderConfiguration, List<Pump> pumps) {
        this.recipe = recipe;
        this.orderConfiguration = orderConfiguration;
        this.pumps = pumps;
        this.feasibilityReport = new FeasibilityReport();
        this.feasibleRecipe = new FeasibleRecipe();
        this.feasibleRecipe.setRecipe(recipe);
        this.compute();
    }

    private void compute() {
        this.transformToAmountOfLiquid();
        this.addAdditionalIngredients();
        this.computeIngredientGroupReplacementsAndFeasibleRecipe();
        this.computeRequiredIngredients();
        this.computeTotalAmountOfMl();
        GlassService gService = SpringUtility.getBean(GlassService.class);
        this.feasibilityReport.setFailNoGlass(gService.getDispensingAreaState().isAreaEmpty());
    }

    private void computeTotalAmountOfMl() {
        final int amountInMl = this.feasibleRecipe.getFeasibleProductionSteps().stream()
                .filter(x -> x instanceof AddIngredientsProductionStep)
                .map(x -> (AddIngredientsProductionStep) x)
                .flatMap(x -> x.getStepIngredients().stream())
                .filter(x -> x.getIngredient().getUnit() == Ingredient.Unit.MILLILITER)
                .mapToInt(ProductionStepIngredient::getAmount).sum();
        feasibilityReport.setTotalAmountInMl(amountInMl);
    }

    private void addAdditionalIngredients() {
        List<CocktailOrderConfiguration.Customisations.AdditionalIngredient> additionalIngredients = orderConfiguration
                .getCustomisations().getAdditionalIngredients();
        if(additionalIngredients.isEmpty()) {
            return;
        }

        AddIngredientsProductionStep aiProductionStep = new AddIngredientsProductionStep();
        List<ProductionStepIngredient> productionStepIngredients = new ArrayList<>();
        for(CocktailOrderConfiguration.Customisations.AdditionalIngredient additionalIngredient : additionalIngredients) {
            ProductionStepIngredient psIngredient = new ProductionStepIngredient();
            psIngredient.setAmount(additionalIngredient.getAmount());
            psIngredient.setIngredient(additionalIngredient.getIngredient());
            psIngredient.setScale(false);
            psIngredient.setBoostable(false);
            productionStepIngredients.add(psIngredient);
        }

        aiProductionStep.setStepIngredients(productionStepIngredients);
        recipe.getProductionSteps().add(aiProductionStep);
    }

    private void computeIngredientGroupReplacementsAndFeasibleRecipe() {
        boolean allIngredientGroupsReplaced = true;
        Map<Long, FeasibilityReport.IngredientGroupReplacement> ingredientGroupReplacements = new HashMap<>();
        List<ProductionStep> feasibleProductionSteps = new ArrayList<>();
        for (int i = 0; i < recipe.getProductionSteps().size(); i++) {
            ProductionStep productionStep = recipe.getProductionSteps().get(i);
            if (!(productionStep instanceof AddIngredientsProductionStep)) {
                feasibleProductionSteps.add(productionStep);
                continue;
            }

            //The productionStep that should be added to the feasibleRecipe
            AddIngredientsProductionStep aipStep = (AddIngredientsProductionStep) productionStep;
            //Stores existing productionSteps by the ingredientId
            Map<Long, ProductionStepIngredient> existingProductionStepsByIngredientId = new HashMap<>();

            for (ProductionStepIngredient psIngredient : aipStep.getStepIngredients()) {
                ProductionStepIngredient feasibleProductionStepIngredient = new ProductionStepIngredient();
                feasibleProductionStepIngredient.setAmount(psIngredient.getAmount());
                feasibleProductionStepIngredient.setScale(false);

                //We only replace IngredientGroups
                if (!(psIngredient.getIngredient() instanceof IngredientGroup)) {
                    feasibleProductionStepIngredient.setIngredient(psIngredient.getIngredient());
                    mergeWithExistingProductionStepIngredients(existingProductionStepsByIngredientId, feasibleProductionStepIngredient);
                    continue;
                }

                FeasibilityReport.IngredientGroupReplacement ingredientGroupReplacement = new FeasibilityReport.IngredientGroupReplacement();
                IngredientGroup toReplaceIngredientGroup = (IngredientGroup) psIngredient.getIngredient();
                ingredientGroupReplacement.setIngredientGroup(toReplaceIngredientGroup);
                AddableIngredient addableIngredient = orderConfiguration.getReplacement(psIngredient.getIngredient().getId());

                if (addableIngredient != null) {
                    if(toReplaceIngredientGroup.getAddableIngredientChildren().stream()
                            .noneMatch(x -> x.getId() == addableIngredient.getId())) {
                        throw new IllegalArgumentException(toReplaceIngredientGroup.getName()
                                + " can't be replaced with " + addableIngredient.getName());
                    }
                    feasibleProductionStepIngredient.setIngredient(addableIngredient);
                    ingredientGroupReplacement.setSelectedReplacement(addableIngredient);
                } else {
                    AddableIngredient autoSelectedReplacement = findIngredientGroupReplacement(toReplaceIngredientGroup, this.pumps);
                    ingredientGroupReplacement.setSelectedReplacement(autoSelectedReplacement);
                    if(autoSelectedReplacement != null) {
                        feasibleProductionStepIngredient.setIngredient(autoSelectedReplacement);
                    } else {
                        allIngredientGroupsReplaced = false;
                        feasibleProductionStepIngredient.setIngredient(toReplaceIngredientGroup);
                    }
                }

                ingredientGroupReplacements.put(ingredientGroupReplacement.getIngredientGroup().getId(), ingredientGroupReplacement);
                mergeWithExistingProductionStepIngredients(existingProductionStepsByIngredientId, feasibleProductionStepIngredient);
            }
            AddIngredientsProductionStep feasibleAIPSStep = new AddIngredientsProductionStep();
            feasibleAIPSStep.setStepIngredients(new ArrayList<>(existingProductionStepsByIngredientId.values()));
            feasibleProductionSteps.add(feasibleAIPSStep);
        }
        this.feasibilityReport.setAllIngredientGroupsReplaced(allIngredientGroupsReplaced);
        this.feasibleRecipe.setFeasibleProductionSteps(feasibleProductionSteps);
        this.feasibilityReport.setIngredientGroupReplacements(new ArrayList<>(ingredientGroupReplacements.values()));
    }

    private void computeRequiredIngredients() {
        Map<Ingredient, Integer> neededAmountPerIngredientId = CocktailFactory.getNeededAmountNeededPerIngredient(this.feasibleRecipe);
        Map<Long, List<Pump>> pumpsByIngredientId = this.pumps.stream().filter(x -> x.getCurrentIngredient() != null)
                .collect(Collectors.groupingBy(Pump::getCurrentIngredientId));
        Map<Long, FeasibilityReport.RequiredIngredient> requiredIngredients = new HashMap<>();

        for (Map.Entry<Ingredient, Integer> ingredientAmountPair : neededAmountPerIngredientId.entrySet()) {
            Ingredient ingredient = ingredientAmountPair.getKey();
            int remainingNeededAmount = ingredientAmountPair.getValue();

            if(remainingNeededAmount == 0) {
                continue;
            }

            FeasibilityReport.RequiredIngredient requiredIngredient = new FeasibilityReport.RequiredIngredient();
            requiredIngredient.setIngredient(ingredient);
            requiredIngredient.setAmountRequired(remainingNeededAmount);
            requiredIngredient.setAmountMissing(0);
            requiredIngredients.put(ingredient.getId(), requiredIngredient);

            if (!pumpsByIngredientId.containsKey(ingredient.getId())) {
                //We only check for automated ingredients
                continue;
            }
            for (Pump pump : pumpsByIngredientId.get(ingredient.getId())) {
                if (remainingNeededAmount > pump.getFillingLevelInMl()) {
                    remainingNeededAmount -= pump.getFillingLevelInMl();
                } else {
                    remainingNeededAmount = 0;
                }
            }
            if (remainingNeededAmount > 0) {
                requiredIngredient.setAmountMissing(remainingNeededAmount);
            }
        }
        this.feasibilityReport.setRequiredIngredients(new HashSet<>(requiredIngredients.values()));
    }

    /**
     * Recalculates the amount of liquid for all ingredients in order to sum up to the total wanted amount of liquid.
     * Also applies boosting.
     */
    private void transformToAmountOfLiquid() {
        List<ProductionStepIngredient> productionStepIngredients = recipe.getProductionSteps().stream()
                .filter(x -> x instanceof AddIngredientsProductionStep)
                .map(x -> (AddIngredientsProductionStep) x)
                .flatMap(x -> x.getStepIngredients().stream()).toList();

        //apply boost
        if(!recipe.isBoostable() && orderConfiguration.getCustomisations().getBoost() != 100) {
            throw new IllegalArgumentException("Recipe not boostable!");
        }

        final float boostMultiplier = orderConfiguration.getCustomisations().getBoost() / 100f;
        productionStepIngredients.stream().filter(ProductionStepIngredient::isBoostable)
                .forEach(x -> x.setAmount(Math.round(x.getAmount() * boostMultiplier)));

        //scale
        int liquidAmountScaled = productionStepIngredients.stream()
                .filter(x -> x.getIngredient().getUnit() == Ingredient.Unit.MILLILITER)
                .filter(ProductionStepIngredient::isScale)
                .mapToInt(ProductionStepIngredient::getAmount).sum();
        int liquidAmountUnscaled = productionStepIngredients.stream()
                .filter(x -> x.getIngredient().getUnit() == Ingredient.Unit.MILLILITER)
                .filter(x -> !x.isScale())
                .mapToInt(ProductionStepIngredient::getAmount).sum();
        int liquidAmountToBeScaledTo = orderConfiguration.getAmountOrderedInMl() - liquidAmountUnscaled;
        if(liquidAmountScaled <= 0) {
            return;
        }
        double multiplier;
        if(liquidAmountToBeScaledTo <= 0) {
            multiplier = 0;
        } else {
            multiplier = liquidAmountToBeScaledTo / ((double) liquidAmountScaled);
        }

        for(ProductionStep pStep : recipe.getProductionSteps()) {
            if(pStep instanceof AddIngredientsProductionStep) {
                AddIngredientsProductionStep addIPStep = (AddIngredientsProductionStep) pStep;
                for(ProductionStepIngredient psi : addIPStep.getStepIngredients()) {
                    if(psi.isScale()) {
                        psi.setAmount((int) (psi.getAmount() * multiplier));
                    }
                }
            }
        }
    }

    /**
     * Adds or merges a productionStep to an existing HashMap that contains all existing productionSteps.
     * @param existingProductionSteps a map with all existing productionSteps by the ingredientId
     * @param toMerge The productionStep to merge
     * @return true if productionSteps have been merged
     */
    private boolean mergeWithExistingProductionStepIngredients(Map<Long, ProductionStepIngredient> existingProductionSteps, ProductionStepIngredient toMerge) {
        ProductionStepIngredient existingProductionStep = existingProductionSteps.get(toMerge.getIngredient().getId());
        if(existingProductionStep == null) {
            existingProductionSteps.put(toMerge.getIngredient().getId(), toMerge);
            return false;
        }
        existingProductionStep.setAmount(existingProductionStep.getAmount() + toMerge.getAmount());
        return true;
    }

    private AddableIngredient findIngredientGroupReplacement(IngredientGroup ingredientGroup, List<Pump> pumps) {
        class IngredientWithRemainingLiquid {
            AddableIngredient ingredient;
            int fillingLevel;
        }
        Map<Long, Integer> ingredientFillingLevel = pumps.stream()
                .filter(x -> x.getCurrentIngredientId() != null)
                .collect(Collectors.toMap(Pump::getCurrentIngredientId, Pump::getFillingLevelInMl, Integer::sum));
        List<AddableIngredient> inBar = new ArrayList<>();
        List<IngredientWithRemainingLiquid> onPump = new ArrayList<>();
        for (AddableIngredient addableIngredient : ingredientGroup.getAddableIngredientChildren()) {
            if(addableIngredient.isOnPump()) {
                IngredientWithRemainingLiquid ingredientWithRemainingLiquid = new IngredientWithRemainingLiquid();
                ingredientWithRemainingLiquid.ingredient = addableIngredient;
                ingredientWithRemainingLiquid.fillingLevel = ingredientFillingLevel.getOrDefault(addableIngredient.getId(), 0);
                onPump.add(ingredientWithRemainingLiquid);
            }
            if(addableIngredient.isInBar()) {
                inBar.add(addableIngredient);
            }
        }
        onPump.sort(Comparator.comparingInt(x -> x.fillingLevel));
        if(onPump.isEmpty() && inBar.isEmpty()) {
            return null;
        }
        //First element
        if(onPump.isEmpty()) {
            return inBar.get(0);
        }
        //Last element (the one with most liquid left
        return onPump.get(onPump.size() - 1).ingredient;
    }

    public FeasibilityReport getFeasibilityReport() {
        return this.feasibilityReport;
    }

    public FeasibleRecipe getFeasibleRecipe() {
        if (!this.getFeasibilityReport().isFeasible()) {
            return null;
        }
        return this.feasibleRecipe;
    }
}
