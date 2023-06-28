package net.alex9849.cocktailmaker.model;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.IngredientGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeasibilityReport {
    private List<List<IngredientGroupReplacement>> ingredientGroupReplacements = new ArrayList<>();
    private Set<RequiredIngredient> requiredIngredients = new HashSet<>();
    private boolean allIngredientGroupsReplaced;
    private int totalAmountInMl;

    public List<List<IngredientGroupReplacement>> getIngredientGroupReplacements() {
        return ingredientGroupReplacements;
    }

    public void setIngredientGroupReplacements(List<List<IngredientGroupReplacement>> ingredientGroupReplacements) {
        this.ingredientGroupReplacements = ingredientGroupReplacements;
    }

    public Set<RequiredIngredient> getRequiredIngredients() {
        return requiredIngredients;
    }

    public void setRequiredIngredients(Set<RequiredIngredient> requiredIngredients) {
        this.requiredIngredients = requiredIngredients;
    }

    public boolean isAllIngredientGroupsReplaced() {
        return allIngredientGroupsReplaced;
    }

    public void setAllIngredientGroupsReplaced(boolean allIngredientGroupsReplaced) {
        this.allIngredientGroupsReplaced = allIngredientGroupsReplaced;
    }

    public int getTotalAmountInMl() {
        return totalAmountInMl;
    }

    public void setTotalAmountInMl(int totalAmountInMl) {
        this.totalAmountInMl = totalAmountInMl;
    }

    public boolean isFeasible() {
        return allIngredientGroupsReplaced && requiredIngredients.stream().allMatch(x -> x.amountMissing == 0);
    }

    @Getter @Setter
    public static class RequiredIngredient {
        private Ingredient ingredient;
        private int amountRequired;
        private int amountMissing;
    }

    @Getter @Setter
    public static class IngredientGroupReplacement {
        private IngredientGroup ingredientGroup;
        private AddableIngredient selectedReplacement;
    }
}
