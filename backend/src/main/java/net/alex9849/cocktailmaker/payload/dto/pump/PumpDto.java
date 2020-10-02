package net.alex9849.cocktailmaker.payload.dto.pump;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.payload.dto.recipe.IngredientDto;
import net.alex9849.cocktailmaker.service.cocktailfactory.PumpCleanService;
import org.springframework.beans.BeanUtils;

public class PumpDto {

    private Long id;

    private int timePerClInMs;

    private int tubeCapacityInMl;

    private int gpioPin;

    private IngredientDto currentIngredient;

    private boolean isCleaning;

    public PumpDto() {}

    public PumpDto(Pump pump) {
        BeanUtils.copyProperties(pump, this);
        this.isCleaning = PumpCleanService.getInstance().isCleaning(pump);
        if(pump.getCurrentIngredient() != null) {
            this.currentIngredient = new IngredientDto(pump.getCurrentIngredient());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public boolean isCleaning() {
        return isCleaning;
    }

    public void setGpioPin(int gpioPin) {
        this.gpioPin = gpioPin;
    }

    public IngredientDto getCurrentIngredient() {
        return currentIngredient;
    }

    public void setCurrentIngredient(IngredientDto currentIngredient) {
        this.currentIngredient = currentIngredient;
    }
}
