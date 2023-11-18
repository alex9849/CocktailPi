package net.alex9849.cocktailpi.service.pumps.cocktailfactory.productionstepworker;

import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;

import java.util.List;

public class PumpStepIngredient {
    private final Ingredient ingredient;
    private final int amount;
    private final List<Pump> applicablePumps;

    public PumpStepIngredient(Ingredient ingredient, int amount, List<Pump> applicablePumps) {
        this.ingredient = ingredient;
        this.amount = amount;
        this.applicablePumps = applicablePumps;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getAmount() {
        return amount;
    }

    public List<Pump> getApplicablePumps() {
        return applicablePumps;
    }
}
