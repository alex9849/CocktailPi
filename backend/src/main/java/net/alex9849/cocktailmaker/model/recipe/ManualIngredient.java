package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Entity
public class ManualIngredient extends Ingredient {

    @Enumerated(EnumType.STRING)
    private Unit unit;

    @NotNull
    private boolean addToFillingQuantity;

    @Override
    public Type getType() {
        return Type.MANUAL;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public boolean isAddToFillingQuantity() {
        return addToFillingQuantity;
    }

    public void setAddToFillingQuantity(boolean addToFillingQuantity) {
        this.addToFillingQuantity = addToFillingQuantity;
    }


}
