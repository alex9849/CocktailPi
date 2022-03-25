package net.alex9849.cocktailmaker.model;

import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;

import java.util.List;

public class FeasibilityReport {
    private List<InsufficientIngredient> insufficientIngredients;

    public List<InsufficientIngredient> getInsufficientIngredients() {
        return insufficientIngredients;
    }

    public void setInsufficientIngredients(List<InsufficientIngredient> insufficientIngredients) {
        this.insufficientIngredients = insufficientIngredients;
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
