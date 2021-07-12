package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Entity()
@DiscriminatorValue("ManualIngredient")
public class ManualIngredient extends Ingredient {

    @Enumerated(EnumType.STRING)
    private Unit unit;

    @NotNull
    private boolean addToVolume;

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public boolean isAddToVolume() {
        return addToVolume;
    }

    public void setAddToVolume(boolean addToVolume) {
        this.addToVolume = addToVolume;
    }
}
