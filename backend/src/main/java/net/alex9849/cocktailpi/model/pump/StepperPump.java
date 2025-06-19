package net.alex9849.cocktailpi.model.pump;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailpi.model.gpio.HardwarePin;
import net.alex9849.motorlib.motor.StepperDriver;
import net.alex9849.motorlib.motor.AcceleratingStepper;
import net.alex9849.motorlib.motor.IStepperMotor;
import net.alex9849.motorlib.pin.IOutputPin;
import net.alex9849.motorlib.pin.PinState;

@DiscriminatorValue("stepper")
public class StepperPump extends Pump {
    private AcceleratingStepper stepperDriver;
    private HardwarePin enableHwPin;
    private HardwarePin stepHwPin;
    private Integer stepsPerCl;
    private Integer maxStepsPerSecond;
    private Integer acceleration;

    public HardwarePin getEnablePin() {
        return enableHwPin;
    }

    public void setEnablePin(HardwarePin enableHwPin) {
        this.enableHwPin = enableHwPin;
        shutdownDriver();
    }

    public HardwarePin getStepPin() {
        return stepHwPin;
    }

    public void setStepPin(HardwarePin stepHwPin) {
        this.stepHwPin = stepHwPin;
        shutdownDriver();
    }

    public Integer getStepsPerCl() {
        return stepsPerCl;
    }

    public void setStepsPerCl(Integer stepsPerCl) {
        this.stepsPerCl = stepsPerCl;
        shutdownDriver();
    }

    public Integer getMaxStepsPerSecond() {
        return maxStepsPerSecond;
    }

    public void setMaxStepsPerSecond(Integer maxStepsPerSecond) {
        this.maxStepsPerSecond = maxStepsPerSecond;
        shutdownDriver();
    }

    public Integer getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Integer acceleration) {
        this.acceleration = acceleration;
        shutdownDriver();
    }

    public void shutdownDriver() {
        if(this.stepperDriver != null) {
            this.stepperDriver.shutdown();
            this.stepperDriver = null;
        }
    }

    @Override
    protected boolean isHwPinsCompleted() {
        return this.enableHwPin != null && this.stepHwPin != null;
    }

    @Override
    protected boolean isCalibrationCompleted() {
        return this.acceleration != null && this.stepsPerCl != null && this.maxStepsPerSecond != null && this.getTubeCapacityInMl() != null;
    }

    @Override
    public boolean isCanControlDirection() {
        return true;
    }

    public AcceleratingStepper getMotorDriver() {
        if(!isCanPump()) {
            throw new IllegalStateException("Motor not ready for pumping!");
        }
        if(this.stepperDriver == null) {

            IOutputPin enablePin = getEnablePin().getOutputPin();
            IOutputPin stepPin = getStepPin().getOutputPin();
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

            IStepperMotor motor = new StepperDriver(enablePin, stepPin, dirPin);
            this.stepperDriver = new AcceleratingStepper(motor);
            this.stepperDriver.setMaxSpeed(getMaxStepsPerSecond());
            this.stepperDriver.setAcceleration(getAcceleration());
        }
        return this.stepperDriver;
    }

    @Override
    public boolean isCanPump() {
        return this.enableHwPin != null && this.stepHwPin != null &&
                this.maxStepsPerSecond != null && this.acceleration != null && this.stepsPerCl != null;
    }
}
