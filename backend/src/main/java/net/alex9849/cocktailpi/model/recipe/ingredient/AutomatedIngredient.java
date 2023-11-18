package net.alex9849.cocktailpi.model.recipe.ingredient;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.service.pumps.PumpDataService;
import net.alex9849.cocktailpi.utils.SpringUtility;

@DiscriminatorValue("AutomatedIngredient")
public class AutomatedIngredient extends AddableIngredient {
    private double pumpTimeMultiplier;
    private Boolean isOnPump;
    private int bottleSize;

    public double getPumpTimeMultiplier() {
        return pumpTimeMultiplier;
    }

    public void setPumpTimeMultiplier(double pumpTimeMultiplier) {
        this.pumpTimeMultiplier = pumpTimeMultiplier;
    }

    public int getBottleSize() {
        return bottleSize;
    }

    public void setBottleSize(int bottleSize) {
        this.bottleSize = bottleSize;
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
