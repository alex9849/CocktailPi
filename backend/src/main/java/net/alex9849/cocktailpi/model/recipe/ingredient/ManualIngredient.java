package net.alex9849.cocktailpi.model.recipe.ingredient;

import jakarta.persistence.DiscriminatorValue;

@DiscriminatorValue("ManualIngredient")
public class ManualIngredient extends AddableIngredient {
    private Unit unit;
    private Integer bottleSize;

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Integer getBottleSize() {
        return bottleSize;
    }

    public void setBottleSize(Integer bottleSize) {
        this.bottleSize = bottleSize;
    }

    @Override
    public boolean isOnPump() {
        return false;
    }
}
