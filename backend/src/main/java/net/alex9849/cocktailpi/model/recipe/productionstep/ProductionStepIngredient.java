package net.alex9849.cocktailpi.model.recipe.productionstep;

import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;

public class ProductionStepIngredient {

    private Ingredient ingredient;
    private int amount;
    private boolean scale;
    private boolean boostable;

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isScale() {
        return scale;
    }

    public void setScale(boolean scale) {
        this.scale = scale;
    }

    public boolean isBoostable() {
        return boostable;
    }

    public void setBoostable(boolean boostable) {
        this.boostable = boostable;
    }
}
