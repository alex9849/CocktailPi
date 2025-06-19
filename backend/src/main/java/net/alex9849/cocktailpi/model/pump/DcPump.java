package net.alex9849.cocktailpi.model.pump;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.motorlib.motor.DCMotor;
import net.alex9849.motorlib.pin.IOutputPin;
import net.alex9849.motorlib.pin.PinState;

@DiscriminatorValue("dc")
public class DcPump extends OnOffPump {
    private Integer timePerClInMs;
    private DCMotor motorDriver;

    public Integer getTimePerClInMs() {
        return timePerClInMs;
    }

    public void setTimePerClInMs(Integer timePerClInMs) {
        this.timePerClInMs = timePerClInMs;
    }

    @Override
    public boolean isCanControlDirection() {
        return true;
    }

    public DCMotor getMotorDriver() {
        if(!isCanPump()) {
            throw new IllegalStateException("Motor not ready for pumping!");
        }
        if(motorDriver == null) {
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

                @Override
                public void digitalWriteAndWait(PinState state) {

                }

                @Override
                public void setWaitAfterWriteTimeNs(long waitAfterWriteTimeNs) {

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

    public void shutdownDriver() {
        if(this.motorDriver != null) {
            this.motorDriver.shutdown();
            this.motorDriver = null;
        }
    }

    @Override
    protected boolean isCalibrationCompleted() {
        return this.timePerClInMs != null && this.getTubeCapacityInMl() != null;
    }

    @Override
    public boolean isCanPump() {
        return super.isHwPinsCompleted() && this.timePerClInMs != null;
    }
}
