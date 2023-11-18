package net.alex9849.cocktailpi.model.recipe;

import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStep;

import java.util.List;

public class FeasibleRecipe {
    private Recipe recipe;
    private List<ProductionStep> feasibleProductionSteps;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public List<ProductionStep> getFeasibleProductionSteps() {
        return feasibleProductionSteps;
    }

    public void setFeasibleProductionSteps(List<ProductionStep> feasibleProductionSteps) {
        this.feasibleProductionSteps = feasibleProductionSteps;
    }
}
