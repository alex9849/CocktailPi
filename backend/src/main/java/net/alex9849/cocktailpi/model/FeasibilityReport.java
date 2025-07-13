package net.alex9849.cocktailpi.model;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailpi.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.IngredientGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeasibilityReport {
    @Getter @Setter
    private List<IngredientGroupReplacement> ingredientGroupReplacements = new ArrayList<>();
    @Getter @Setter
    private Set<RequiredIngredient> requiredIngredients = new HashSet<>();
    @Getter @Setter
    private boolean failNoGlass;
    @Getter @Setter
    private boolean allIngredientGroupsReplaced;
    @Getter @Setter
    private int totalAmountInMl;

    public boolean isFeasible() {
        return !failNoGlass && allIngredientGroupsReplaced && requiredIngredients.stream().allMatch(x -> x.amountMissing == 0);
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
