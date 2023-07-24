package net.alex9849.cocktailmaker.model.pump;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailmaker.hardware.IGpioController;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import net.alex9849.motorlib.DCMotor;
import net.alex9849.motorlib.IMotorPin;

import java.util.Objects;

@DiscriminatorValue("dc")
public class DcPump extends Pump {
    private Integer timePerClInMs;
    private Integer pin;
    private Boolean isPowerStateHigh;
    private DCMotor motorDriver;

    public Integer getTimePerClInMs() {
        return timePerClInMs;
    }

    public void setTimePerClInMs(Integer timePerClInMs) {
        this.timePerClInMs = timePerClInMs;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        if(Objects.equals(this.pin, pin)) {
            return;
        }
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
            IGpioController controller = SpringUtility.getBean(IGpioController.class);
            IMotorPin runPin = controller.getGpioPin(getPin());
            IMotorPin dirPin = new IMotorPin() {
                @Override
                public void digitalWrite(PinState value) {
                    //TODO implement direction functionality
                }

                @Override
                public boolean isHigh() {
                    return false;
                }
            };
            motorDriver = new DCMotor(runPin, dirPin, isPowerStateHigh()? IMotorPin.PinState.HIGH : IMotorPin.PinState.LOW);
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

    private void resetDriver() {
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
