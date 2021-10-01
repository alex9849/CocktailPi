package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("ManualIngredient")
public class ManualIngredient extends Ingredient {
    private Unit unit;
    private boolean addToVolume;
    private boolean scaleToVolume;

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

    public boolean isScaleToVolume() {
        return scaleToVolume;
    }

    public void setScaleToVolume(boolean scaleToVolume) {
        this.scaleToVolume = scaleToVolume;
    }
}
