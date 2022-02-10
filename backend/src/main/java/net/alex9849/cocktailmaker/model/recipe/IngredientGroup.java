package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("IngredientGroup")
public class IngredientGroup extends Ingredient {

    public Unit getUnit() {
        return Unit.MILLILITER;
    }

    @Override
    public boolean isInBar() {
        //Todo
        return false;
    }
}
