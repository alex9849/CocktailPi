package net.alex9849.cocktailmaker.model.pump;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailmaker.model.gpio.GpioBoard;
import net.alex9849.motorlib.StepperDriver;
import net.alex9849.motorlib.motor.AcceleratingStepper;
import net.alex9849.motorlib.motor.IStepperMotor;
import net.alex9849.motorlib.pin.IOutputPin;
import net.alex9849.motorlib.pin.PinState;

@DiscriminatorValue("stepper")
public class StepperPump extends Pump {
    private AcceleratingStepper stepperDriver;
    private GpioBoard.Pin enablePin;
    private GpioBoard.Pin stepPin;
    private Integer stepsPerCl;
    private Integer maxStepsPerSecond;
    private Integer acceleration;

    public GpioBoard.Pin getEnablePin() {
        return enablePin;
    }

    public void setEnablePin(GpioBoard.Pin enablePin) {
        this.enablePin = enablePin;
        resetDriver();
    }

    public GpioBoard.Pin getStepPin() {
        return stepPin;
    }

    public void setStepPin(GpioBoard.Pin stepPin) {
        this.stepPin = stepPin;
        resetDriver();
    }

    public Integer getStepsPerCl() {
        return stepsPerCl;
    }

    public void setStepsPerCl(Integer stepsPerCl) {
        this.stepsPerCl = stepsPerCl;
        resetDriver();
    }

    public Integer getMaxStepsPerSecond() {
        return maxStepsPerSecond;
    }

    public void setMaxStepsPerSecond(Integer maxStepsPerSecond) {
        this.maxStepsPerSecond = maxStepsPerSecond;
        resetDriver();
    }

    public Integer getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Integer acceleration) {
        this.acceleration = acceleration;
        resetDriver();
    }

    protected void resetDriver() {
        if(this.stepperDriver != null) {
            this.stepperDriver.shutdown();
            this.stepperDriver = null;
        }
    }

    @Override
    protected boolean isHwPinsCompleted() {
        return this.enablePin != null && this.stepPin != null;
    }

    @Override
    protected boolean isCalibrationCompleted() {
        return this.acceleration != null && this.stepsPerCl != null && this.maxStepsPerSecond != null && this.getTubeCapacityInMl() != null;
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
        return this.enablePin != null && this.stepPin != null &&
                this.maxStepsPerSecond != null && this.acceleration != null && this.stepsPerCl != null;
    }
}
