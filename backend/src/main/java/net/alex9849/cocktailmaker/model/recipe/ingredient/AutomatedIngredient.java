package net.alex9849.cocktailmaker.model.recipe.ingredient;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.service.pumps.PumpDataService;
import net.alex9849.cocktailmaker.utils.SpringUtility;

@DiscriminatorValue("AutomatedIngredient")
public class AutomatedIngredient extends AddableIngredient {
    private double pumpTimeMultiplier;
    private Boolean isOnPump;

    public double getPumpTimeMultiplier() {
        return pumpTimeMultiplier;
    }

    public void setPumpTimeMultiplier(double pumpTimeMultiplier) {
        this.pumpTimeMultiplier = pumpTimeMultiplier;
    }

    public boolean isOnPump() {
        if(isOnPump == null) {
            PumpDataService pRepository = SpringUtility.getBean(PumpDataService.class);
            isOnPump = pRepository.findPumpsWithIngredient(getId()).stream().anyMatch(Pump::isCompleted);
        }
        return isOnPump;
    }

    @Override
    public Unit getUnit() {
        return Unit.MILLILITER;
    }
}
