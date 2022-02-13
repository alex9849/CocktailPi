package net.alex9849.cocktailmaker.model;

import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.repository.IngredientRepository;
import net.alex9849.cocktailmaker.utils.SpringUtility;

public class Pump {
    private long id;
    private int timePerClInMs;
    private int tubeCapacityInMl;
    private int bcmPin;
    private int fillingLevelInMl;
    private Long currentIngredientId;
    private AutomatedIngredient currentIngredient;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTimePerClInMs() {
        return timePerClInMs;
    }

    public void setTimePerClInMs(int timePerClInMs) {
        this.timePerClInMs = timePerClInMs;
    }

    public int getTubeCapacityInMl() {
        return tubeCapacityInMl;
    }

    public void setTubeCapacityInMl(int tubeCapacityInMl) {
        this.tubeCapacityInMl = tubeCapacityInMl;
    }

    public int getBcmPin() {
        return bcmPin;
    }

    public void setBcmPin(int bcmPin) {
        this.bcmPin = bcmPin;
    }

    public AutomatedIngredient getCurrentIngredient() {
        if(currentIngredientId != null && currentIngredient == null) {
            IngredientRepository pRepository = SpringUtility.getBean(IngredientRepository.class);
            Ingredient ingredient = pRepository.findById(currentIngredientId).orElse(null);

            if(!(ingredient instanceof AutomatedIngredient)) {
                currentIngredientId = null;
                return null;
            }
            currentIngredient = (AutomatedIngredient) ingredient;
        }
        return currentIngredient;
    }

    public Long getCurrentIngredientId() {
        return currentIngredientId;
    }

    public void setCurrentIngredient(AutomatedIngredient currentIngredient) {
        this.currentIngredient = currentIngredient;
        this.currentIngredientId = (currentIngredient != null)? currentIngredient.getId() : null;
    }

    public int getFillingLevelInMl() {
        return fillingLevelInMl;
    }

    public void setFillingLevelInMl(int fillingLevelInMl) {
        this.fillingLevelInMl = fillingLevelInMl;
    }

    public void setCurrentIngredientId(Long currentIngredientId) {
        this.currentIngredientId = currentIngredientId;
        this.currentIngredient = null;
    }
}
