package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;

public class ManualStepProgress extends StepProgress {
    private RecipeIngredient ingredientToBeAdded;

    public RecipeIngredient getIngredientToBeAdded() {
        return ingredientToBeAdded;
    }

    public void setIngredientToBeAdded(RecipeIngredient ingredientToBeAdded) {
        this.ingredientToBeAdded = ingredientToBeAdded;
    }
}
