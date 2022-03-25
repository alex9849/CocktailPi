package net.alex9849.cocktailmaker.model.recipe;

import net.alex9849.cocktailmaker.model.recipe.ingredient.AddableIngredient;

import java.util.HashMap;
import java.util.Map;

public class IngredientGroupReplacements {
    //ProductionStep, IngredientGroupId, Replacement
    private Map<Long, Map<Long, AddableIngredient>> productionStepReplacements;

    public Map<Long, Map<Long, AddableIngredient>> getProductionStepReplacements() {
        return productionStepReplacements;
    }

    public void setProductionStepReplacements(Map<Long, Map<Long, AddableIngredient>> productionStepReplacements) {
        this.productionStepReplacements = productionStepReplacements;
    }

    public AddableIngredient getReplacement(long productionStep, long ingredientGroupId) {
        return productionStepReplacements
                .getOrDefault(productionStep, new HashMap<>())
                .getOrDefault(ingredientGroupId, null);
    }
}
