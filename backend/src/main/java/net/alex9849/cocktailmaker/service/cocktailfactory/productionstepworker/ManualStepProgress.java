package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;

import java.util.List;

public class ManualStepProgress extends StepProgress {
    private List<RecipeIngredient> ingredientsToBeAdded;

    public List<RecipeIngredient> getIngredientsToBeAdded() {
        return ingredientsToBeAdded;
    }

    public void setIngredientsToBeAdded(List<RecipeIngredient> ingredientsToBeAdded) {
        this.ingredientsToBeAdded = ingredientsToBeAdded;
    }
}
