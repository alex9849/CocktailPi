package net.alex9849.cocktailpi.model.pump;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailpi.model.gpio.HardwarePin;
import net.alex9849.motorlib.motor.DCMotor;
import net.alex9849.motorlib.motor.StepperDriver;
import net.alex9849.motorlib.motor.AcceleratingStepper;
import net.alex9849.motorlib.motor.IStepperMotor;
import net.alex9849.motorlib.pin.IOutputPin;
import net.alex9849.motorlib.pin.PinState;

import java.util.Objects;

@DiscriminatorValue("stepper")
public class StepperPump extends Pump {
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
    }

    public HardwarePin getStepPin() {
        return stepHwPin;
    }

    public void setStepPin(HardwarePin stepHwPin) {
        this.stepHwPin = stepHwPin;
    }

    public Integer getStepsPerCl() {
        return stepsPerCl;
    }

    public void setStepsPerCl(Integer stepsPerCl) {
        this.stepsPerCl = stepsPerCl;
    }

    public Integer getMaxStepsPerSecond() {
        return maxStepsPerSecond;
    }

    public void setMaxStepsPerSecond(Integer maxStepsPerSecond) {
        this.maxStepsPerSecond = maxStepsPerSecond;
    }

    public Integer getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Integer acceleration) {
        this.acceleration = acceleration;
    }

    @Override
    public boolean equalDriverProperties(Pump other) {
        if(other instanceof StepperPump stepperOther) {
            return Objects.equals(this.enableHwPin, stepperOther.enableHwPin)
                    && Objects.equals(this.stepHwPin, stepperOther.stepHwPin)
                    && Objects.equals(this.maxStepsPerSecond, stepperOther.maxStepsPerSecond)
                    && Objects.equals(this.acceleration, stepperOther.acceleration)
                    && Objects.equals(this.stepsPerCl, stepperOther.stepsPerCl);
        }
        return false;
    }

    @Override
    protected boolean isHwPinsCompleted() {
        return this.enableHwPin != null && this.stepHwPin != null;
    }

    @Override
    protected boolean isCalibrationCompleted() {
        return this.acceleration != null && this.stepsPerCl != null && this.maxStepsPerSecond != null && this.getTubeCapacityInMl() != null;
    }

    public AcceleratingStepper getMotorDriver() {
        return (AcceleratingStepper) super.getMotorDriver();
    }

    public AcceleratingStepper genMotorDriver() {
        IOutputPin enablePin = getEnablePin().getOutputPin();
        IOutputPin stepPin = getStepPin().getOutputPin();
        IOutputPin dirPin = new IOutputPin() {
            @Override
            public void digitalWrite(PinState value) {}

            @Override
            public boolean isHigh() {
                return false;
            }

            @Override
            public void digitalWriteAndWait(PinState state) {}

            @Override
            public void setWaitAfterWriteTimeNs(long waitAfterWriteTimeNs) {}
        };

        IStepperMotor motor = new StepperDriver(enablePin, stepPin, dirPin);
        AcceleratingStepper aMotor = new AcceleratingStepper(motor);
        aMotor.setMaxSpeed(getMaxStepsPerSecond());
        aMotor.setAcceleration(getAcceleration());
        return aMotor;
    }

    @Override
    public boolean isCanPump() {
        return this.enableHwPin != null && this.stepHwPin != null &&
                this.maxStepsPerSecond != null && this.acceleration != null && this.stepsPerCl != null;
    }
}
