package net.alex9849.cocktailmaker.payload.dto.pump;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.payload.dto.recipe.AutomatedIngredientDto;
import net.alex9849.cocktailmaker.service.PumpService;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PumpDto {

    private long id;

    @NotNull
    @Min(1)
    private int timePerClInMs;

    @NotNull
    @Min(1)
    private int tubeCapacityInMl;

    @NotNull
    @Min(0) @Max(31)
    private int gpioPin;

    @NotNull
    @Min(0)
    private int fillingLevelInMl;

    private AutomatedIngredientDto currentIngredient;

    private boolean isCleaning;

    public PumpDto() {}

    public PumpDto(Pump pump) {
        BeanUtils.copyProperties(pump, this);
        this.isCleaning = PumpService.getInstance().isCleaning(pump);
        if(pump.getCurrentIngredient() != null) {
            this.currentIngredient = new AutomatedIngredientDto(pump.getCurrentIngredient());
        }
    }

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

    public boolean isCleaning() {
        return isCleaning;
    }

    public void setGpioPin(int gpioPin) {
        this.gpioPin = gpioPin;
    }

    public AutomatedIngredientDto getCurrentIngredient() {
        return currentIngredient;
    }

    public void setCurrentIngredient(AutomatedIngredientDto currentIngredient) {
        this.currentIngredient = currentIngredient;
    }

    public int getFillingLevelInMl() {
        return fillingLevelInMl;
    }

    public void setFillingLevelInMl(int fillingLevelInMl) {
        this.fillingLevelInMl = fillingLevelInMl;
    }
}
