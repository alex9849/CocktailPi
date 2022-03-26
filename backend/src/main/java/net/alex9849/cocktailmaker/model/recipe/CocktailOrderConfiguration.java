package net.alex9849.cocktailmaker.model.recipe;

import net.alex9849.cocktailmaker.model.recipe.ingredient.AddableIngredient;

import java.util.HashMap;
import java.util.Map;

public class CocktailOrderConfiguration {
    //ProductionStep, IngredientGroupId, Replacement
    private Map<Long, Map<Long, AddableIngredient>> productionStepReplacements = new HashMap<>();
    private Integer amountOrderedInMl;

    public Map<Long, Map<Long, AddableIngredient>> getProductionStepReplacements() {
        return productionStepReplacements;
    }

    public void setProductionStepReplacements(Map<Long, Map<Long, AddableIngredient>> productionStepReplacements) {
        this.productionStepReplacements = productionStepReplacements;
    }

    public Integer getAmountOrderedInMl() {
        return amountOrderedInMl;
    }

    public void setAmountOrderedInMl(int amountOrderedInMl) {
        this.amountOrderedInMl = amountOrderedInMl;
    }

    public AddableIngredient getReplacement(long productionStep, long ingredientGroupId) {
        return productionStepReplacements
                .getOrDefault(productionStep, new HashMap<>())
                .getOrDefault(ingredientGroupId, null);
    }
}
