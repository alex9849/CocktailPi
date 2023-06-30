package net.alex9849.cocktailmaker.model.pump;

import net.alex9849.cocktailmaker.iface.IGpioController;
import net.alex9849.cocktailmaker.iface.IGpioPin;
import net.alex9849.cocktailmaker.utils.SpringUtility;

import java.util.Objects;

public class DcPump extends Pump {
    private Integer timePerClInMs;
    private Integer bcmPin;
    private Boolean isPowerStateHigh;
    private IGpioPin gpioPin;

    public Integer getTimePerClInMs() {
        return timePerClInMs;
    }

    public void setTimePerClInMs(Integer timePerClInMs) {
        this.timePerClInMs = timePerClInMs;
    }

    public Integer getBcmPin() {
        return bcmPin;
    }

    public void setBcmPin(Integer bcmPin) {
        if(Objects.equals(this.bcmPin, bcmPin)) {
            return;
        }
        this.bcmPin = bcmPin;
        this.gpioPin = null;
    }

    public Boolean isPowerStateHigh() {
        return isPowerStateHigh;
    }

    public void setPowerStateHigh(Boolean isPowerStateHigh) {
        this.isPowerStateHigh = isPowerStateHigh;
    }

    public IGpioPin getGpioPin() {
        if(gpioPin == null) {
            IGpioController controller = SpringUtility.getBean(IGpioController.class);
            gpioPin = controller.getGpioPin(getBcmPin());
        }
        return gpioPin;
    }

    public boolean isRunning() {
        IGpioPin gpioPin = getGpioPin();
        return gpioPin.isHigh() == isPowerStateHigh();
    }

    public int getConvertMlToRuntime(double mlToPump) {
        double multiplier = 1;
        if(getCurrentIngredient() != null) {
            multiplier = getCurrentIngredient().getPumpTimeMultiplier();
        }
        return  (int) (multiplier * mlToPump * this.getTimePerClInMs() / 10d);
    }

    public double getConvertRuntimeToMl(int runtime) {
        if(getCurrentIngredient() == null) {
            return 0;
        }
        return runtime / (getCurrentIngredient().getPumpTimeMultiplier()
                * this.getTimePerClInMs() / 10d);
    }

    public void setRunning(boolean run) {
        if(run == isPowerStateHigh()) {
            getGpioPin().setHigh();
        } else {
            getGpioPin().setLow();
        }
    }

    @Override
    public boolean isCanPump() {
        return this.bcmPin != null && this.timePerClInMs != null && this.isPowerStateHigh != null;
    }
}
