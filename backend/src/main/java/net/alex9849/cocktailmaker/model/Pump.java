package net.alex9849.cocktailmaker.model;

import net.alex9849.cocktailmaker.model.recipe.Ingredient;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "pumps", uniqueConstraints = {
        @UniqueConstraint(columnNames = "gpioPin")})
public class Pump {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    private int timePerClInMs;

    @NotNull
    @Min(1)
    private int syrupTimePerClInMs;

    @NotNull
    @Min(1)
    private int tubeCapacityInMl;

    @NotNull
    @Min(0) @Max(31)
    private int gpioPin;

    @ManyToOne()
    private Ingredient currentIngredient;

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

    public int getSyrupTimePerClInMs() {
        return syrupTimePerClInMs;
    }

    public void setSyrupTimePerClInMs(int syrupTimePerClInMs) {
        this.syrupTimePerClInMs = syrupTimePerClInMs;
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

    public void setCurrentIngredient(Ingredient currentIngredient) {
        this.currentIngredient = currentIngredient;
    }
}
