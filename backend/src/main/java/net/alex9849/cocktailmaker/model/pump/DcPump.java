package net.alex9849.cocktailmaker.model.pump;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailmaker.model.gpio.GpioBoard;
import net.alex9849.cocktailmaker.utils.PinUtils;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import net.alex9849.motorlib.motor.DCMotor;
import net.alex9849.motorlib.pin.IOutputPin;
import net.alex9849.motorlib.pin.PinState;

@DiscriminatorValue("dc")
public class DcPump extends Pump {
    private Integer timePerClInMs;
    private GpioBoard.Pin pin;
    private Boolean isPowerStateHigh;
    private DCMotor motorDriver;

    public Integer getTimePerClInMs() {
        return timePerClInMs;
    }

    public void setTimePerClInMs(Integer timePerClInMs) {
        this.timePerClInMs = timePerClInMs;
    }

    public GpioBoard.Pin getPin() {
        return pin;
    }

    public void setPin(GpioBoard.Pin pin) {
        this.pin = pin;
        this.resetDriver();
    }

    public Boolean isPowerStateHigh() {
        return isPowerStateHigh;
    }

    public void setIsPowerStateHigh(Boolean isPowerStateHigh) {
        this.isPowerStateHigh = isPowerStateHigh;
        this.resetDriver();
    }

    public DCMotor getMotorDriver() {
        if(!isCanPump()) {
            throw new IllegalStateException("Motor not ready for pumping!");
        }
        if(motorDriver == null) {
            PinUtils pinUtils = SpringUtility.getBean(PinUtils.class);
            IOutputPin runPin = getPin().getOutputPin();
            IOutputPin dirPin = new IOutputPin() {
                @Override
                public void digitalWrite(PinState value) {
                    //TODO implement direction functionality
                }

                @Override
                public boolean isHigh() {
                    return false;
                }
            };
            motorDriver = new DCMotor(runPin, dirPin, isPowerStateHigh()? PinState.HIGH : PinState.LOW);
        }
        return motorDriver;
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

    protected void resetDriver() {
        if(this.motorDriver != null) {
            this.motorDriver.shutdown();
            this.motorDriver = null;
        }
    }

    @Override
    public boolean isCanPump() {
        return this.pin != null && this.timePerClInMs != null && this.isPowerStateHigh != null;
    }
}
