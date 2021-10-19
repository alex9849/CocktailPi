package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.recipe.AddIngredient;

import java.util.List;

public class ManualStepProgress extends StepProgress {
    private List<AddIngredient> ingredientsToBeAdded;

    public List<AddIngredient> getIngredientsToBeAdded() {
        return ingredientsToBeAdded;
    }

    public void setIngredientsToBeAdded(List<AddIngredient> ingredientsToBeAdded) {
        this.ingredientsToBeAdded = ingredientsToBeAdded;
    }
}
