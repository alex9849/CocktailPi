package net.alex9849.cocktailpi.service.pumps.cocktailfactory.productionstepworker;

import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;

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
