package net.alex9849.cocktailmaker.model;

import net.alex9849.cocktailmaker.model.recipe.Ingredient;

public class Pump {
    private long id;
    private int timePerClInMs;
    private int tubeCapacityInMl;
    private int gpioPin;
    private Long currentIngredientId;
    private Ingredient currentIngredient;

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

    public int getGpioPin() {
        return gpioPin;
    }

    public void setGpioPin(int gpioPin) {
        this.gpioPin = gpioPin;
    }

    public Ingredient getCurrentIngredient() {
        return currentIngredient;
    }

    public Long getCurrentIngredientId() {
        return currentIngredientId;
    }

    public void setCurrentIngredientId(Long currentIngredientId) {
        this.currentIngredientId = currentIngredientId;
    }

    public void setCurrentIngredient(Ingredient currentIngredient) {
        this.currentIngredient = currentIngredient;
    }
}
