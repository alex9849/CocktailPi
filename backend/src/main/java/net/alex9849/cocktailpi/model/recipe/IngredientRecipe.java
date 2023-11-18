package net.alex9849.cocktailpi.model.recipe;

import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;

public class IngredientRecipe extends Recipe {
    private long mlLeft;
    private Ingredient ingredient;

    public long getMlLeft() {
        return mlLeft;
    }

    public void setMlLeft(long mlLeft) {
        this.mlLeft = mlLeft;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }
}
