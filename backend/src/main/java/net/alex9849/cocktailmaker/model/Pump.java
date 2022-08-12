package net.alex9849.cocktailmaker.model;

import net.alex9849.cocktailmaker.iface.IGpioController;
import net.alex9849.cocktailmaker.iface.IGpioPin;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.repository.IngredientRepository;
import net.alex9849.cocktailmaker.utils.SpringUtility;

import java.util.Objects;

public class Pump {
    private long id;
    private int timePerClInMs;
    private int tubeCapacityInMl;
    private int bcmPin;
    private int fillingLevelInMl;
    private Long currentIngredientId;
    private boolean isPowerStateHigh;
    private boolean isPumpedUp;
    private AutomatedIngredient currentIngredient;
    private IGpioPin gpioPin;

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
        if(this.bcmPin == bcmPin) {
            return;
        }
        this.bcmPin = bcmPin;
        this.gpioPin = null;
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

    public boolean isPowerStateHigh() {
        return isPowerStateHigh;
    }

    public void setIsPowerStateHigh(boolean isPowerStateHigh) {
        this.isPowerStateHigh = isPowerStateHigh;
    }

    public boolean isRunning() {
        IGpioPin gpioPin = getGpioPin();
        return gpioPin.isHigh() == isPowerStateHigh();
    }

    public void setRunning(boolean run) {
        if(run == isPowerStateHigh()) {
            getGpioPin().setHigh();
        } else {
            getGpioPin().setLow();
        }
    }

    public IGpioPin getGpioPin() {
        if(gpioPin == null) {
            IGpioController controller = SpringUtility.getBean(IGpioController.class);
            gpioPin = controller.getGpioPin(getBcmPin());
        }
        return gpioPin;
    }

    public boolean isPumpedUp() {
        return isPumpedUp;
    }

    public void setPumpedUp(boolean pumpedUp) {
        isPumpedUp = pumpedUp;
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
        if(!Objects.equals(this.currentIngredientId, currentIngredientId)) {
            this.currentIngredient = null;
        }
        this.currentIngredientId = currentIngredientId;
    }

    public int getConvertMlToRuntime(int mlToPump) {
        if(getCurrentIngredient() == null) {
            throw new IllegalStateException("Pump got no ingredient assigned!");
        }
        return  (int) (getCurrentIngredient().getPumpTimeMultiplier()
                * mlToPump
                * this.getTimePerClInMs() / 10d);
    }

    public int getConvertRuntimeToMl(int runtime) {
        if(getCurrentIngredient() == null) {
            return 0;
        }
        return  (int) (runtime / (getCurrentIngredient().getPumpTimeMultiplier()
                * this.getTimePerClInMs() / 10d));
    }
}
