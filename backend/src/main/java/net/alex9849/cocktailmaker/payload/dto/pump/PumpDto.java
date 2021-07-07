package net.alex9849.cocktailmaker.payload.dto.pump;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.payload.dto.recipe.IngredientDto;
import net.alex9849.cocktailmaker.service.PumpService;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PumpDto {

    private Long id;

    @NotNull
    @Min(1)
    private int timePerClInMs;

    @NotNull
    @Min(1)
    private int tubeCapacityInMl;

    @NotNull
    @Min(0) @Max(31)
    private int gpioPin;

    private IngredientDto currentIngredient;

    private boolean isCleaning;

    public PumpDto() {}

    public PumpDto(Pump pump) {
        BeanUtils.copyProperties(pump, this);
        this.isCleaning = PumpService.getInstance().isCleaning(pump);
        if(pump.getCurrentIngredient() != null) {
            this.currentIngredient = IngredientDto.toDto(pump.getCurrentIngredient());
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
