package net.alex9849.cocktailmaker.model;

import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.IngredientGroup;

import java.util.List;
import java.util.Map;

public class FeasibilityReport {
    private List<InsufficientIngredient> insufficientIngredients;
    private Map<Long, List<IngredientGroup>> missingIngredientGroupReplacements;

    public Map<Long, List<IngredientGroup>> getMissingIngredientGroupReplacements() {
        return missingIngredientGroupReplacements;
    }

    public void setMissingIngredientGroupReplacements(Map<Long, List<IngredientGroup>> missingIngredientGroupReplacements) {
        this.missingIngredientGroupReplacements = missingIngredientGroupReplacements;
    }

    public List<InsufficientIngredient> getInsufficientIngredients() {
        return insufficientIngredients;
    }

    public void setInsufficientIngredients(List<InsufficientIngredient> insufficientIngredients) {
        this.insufficientIngredients = insufficientIngredients;
    }

    public boolean isFeasible() {
        return this.insufficientIngredients.isEmpty() && this.missingIngredientGroupReplacements.isEmpty();
    }

    public static class InsufficientIngredient {
        private Ingredient ingredient;
        private int amountNeeded;
        private int amountRemaining;

        public Ingredient getIngredient() {
            return ingredient;
        }

        public void setIngredient(Ingredient ingredient) {
            this.ingredient = ingredient;
        }

        public int getAmountNeeded() {
            return amountNeeded;
        }

        public void setAmountNeeded(int amountNeeded) {
            this.amountNeeded = amountNeeded;
        }

        public int getAmountRemaining() {
            return amountRemaining;
        }

        public void setAmountRemaining(int amountRemaining) {
            this.amountRemaining = amountRemaining;
        }
    }
}
