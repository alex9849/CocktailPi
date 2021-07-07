package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
public class AutomatedIngredient extends Ingredient {

    @NotNull
    @Min(1)
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
