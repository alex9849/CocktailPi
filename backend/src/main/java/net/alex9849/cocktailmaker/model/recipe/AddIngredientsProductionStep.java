package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.DiscriminatorValue;
import java.util.List;

@DiscriminatorValue("AddIngredients")
public class AddIngredientsProductionStep implements ProductionStep {
    private List<AddIngredient> recipeIngredients;

    public List<AddIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<AddIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }
}
