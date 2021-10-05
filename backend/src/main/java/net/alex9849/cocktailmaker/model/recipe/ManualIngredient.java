package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("ManualIngredient")
public class ManualIngredient extends Ingredient {
    private Unit unit;

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
