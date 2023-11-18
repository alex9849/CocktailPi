package net.alex9849.cocktailpi.model.recipe.ingredient;

import jakarta.persistence.DiscriminatorValue;

@DiscriminatorValue("ManualIngredient")
public class ManualIngredient extends AddableIngredient {
    private Unit unit;

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public boolean isOnPump() {
        return false;
    }
}
