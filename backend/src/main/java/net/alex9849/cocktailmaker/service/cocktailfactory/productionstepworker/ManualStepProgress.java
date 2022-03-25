package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStepIngredient;

import java.util.List;

public class ManualStepProgress extends StepProgress {
    private List<ProductionStepIngredient> ingredientsToBeAdded;

    public List<ProductionStepIngredient> getIngredientsToBeAdded() {
        return ingredientsToBeAdded;
    }

    public void setIngredientsToBeAdded(List<ProductionStepIngredient> ingredientsToBeAdded) {
        this.ingredientsToBeAdded = ingredientsToBeAdded;
    }
}
