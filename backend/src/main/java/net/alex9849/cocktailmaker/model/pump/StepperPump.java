package net.alex9849.cocktailmaker.model.pump;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailmaker.hardware.IGpioController;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import net.alex9849.motorlib.AcceleratingStepper;
import net.alex9849.motorlib.IMotorPin;
import net.alex9849.motorlib.IStepperMotor;
import net.alex9849.motorlib.StepperDriver;

@DiscriminatorValue("stepper")
public class StepperPump extends Pump {
    private AcceleratingStepper stepperDriver;
    private Integer enablePin;
    private Integer stepPin;
    private Integer stepsPerCl;
    private Integer maxStepsPerSecond;
    private Integer acceleration;

    public Integer getEnablePin() {
        return enablePin;
    }

    public void setEnablePin(Integer enablePin) {
        this.enablePin = enablePin;
        resetDriver();
    }

    public Integer getStepPin() {
        return stepPin;
    }

    public void setStepPin(Integer stepPin) {
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

    public AcceleratingStepper getMotorDriver() {
        if(!isCanPump()) {
            throw new IllegalStateException("Motor not ready for pumping!");
        }
        if(this.stepperDriver == null) {
            IGpioController controller = SpringUtility.getBean(IGpioController.class);
            IMotorPin enablePin = controller.getGpioPin(getEnablePin());
            IMotorPin stepPin = controller.getGpioPin(getStepPin());
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
