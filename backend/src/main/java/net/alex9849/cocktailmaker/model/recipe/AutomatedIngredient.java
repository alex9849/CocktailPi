package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("AutomatedIngredient")
public class AutomatedIngredient extends Ingredient {
    private double pumpTimeMultiplier;

    public double getPumpTimeMultiplier() {
        return pumpTimeMultiplier;
    }

    public void setPumpTimeMultiplier(double pumpTimeMultiplier) {
        this.pumpTimeMultiplier = pumpTimeMultiplier;
    }

    @Override
    public Unit getUnit() {
        return Unit.MILLILITER;
    }
}
