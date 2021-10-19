package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.DiscriminatorValue;
import java.util.List;

@DiscriminatorValue("AddIngredients")
public class AddIngredientsProductionStep implements ProductionStep {
    private List<Ingredient> ingredients;

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
