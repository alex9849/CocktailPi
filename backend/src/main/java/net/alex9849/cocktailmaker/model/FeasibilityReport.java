package net.alex9849.cocktailmaker.model;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailmaker.model.recipe.IngredientGroupReplacements;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.IngredientGroup;

import java.util.*;

public class FeasibilityReport {
    private List<InsufficientIngredient> insufficientIngredients = new ArrayList<>();
    private List<List<IngredientGroupReplacement>> ingredientGroupReplacements = new ArrayList<>();
    private Set<Ingredient> ingredientsToAddManually = new HashSet<>();
    private boolean isFeasible = false;

    public List<List<IngredientGroupReplacement>> getIngredientGroupReplacements() {
        return ingredientGroupReplacements;
    }

    public void setIngredientGroupReplacements(List<List<IngredientGroupReplacement>> ingredientGroupReplacements) {
        this.ingredientGroupReplacements = ingredientGroupReplacements;
    }

    public Set<Ingredient> getIngredientsToAddManually() {
        return ingredientsToAddManually;
    }

    public void setIngredientsToAddManually(Set<Ingredient> ingredientsToAddManually) {
        this.ingredientsToAddManually = ingredientsToAddManually;
    }

    public List<InsufficientIngredient> getInsufficientIngredients() {
        return insufficientIngredients;
    }

    public void setInsufficientIngredients(List<InsufficientIngredient> insufficientIngredients) {
        this.insufficientIngredients = insufficientIngredients;
    }

    public void setFeasible(boolean feasible) {
        isFeasible = feasible;
    }

    public boolean isFeasible() {
        return isFeasible;
    }

    @Getter @Setter
    public static class InsufficientIngredient {
        private Ingredient ingredient;
        private int amountNeeded;
        private int amountRemaining;
    }

    @Getter @Setter
    public static class IngredientGroupReplacement {
        private IngredientGroup ingredientGroup;
        private boolean replacementAutoSelected;
        private AddableIngredient selectedReplacement;
    }
}
