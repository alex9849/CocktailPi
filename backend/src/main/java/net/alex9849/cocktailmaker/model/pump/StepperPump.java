package net.alex9849.cocktailmaker.model.pump;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("StepperPump")
public class StepperPump extends Pump {
    Integer enablePin;
    Integer stepPin;
    Integer stepsPerCl;
    Integer minStepDeltaInMs;
    Integer acceleration;

    public Integer getEnablePin() {
        return enablePin;
    }

    public void setEnablePin(Integer enablePin) {
        this.enablePin = enablePin;
    }

    public Integer getStepPin() {
        return stepPin;
    }

    public void setStepPin(Integer stepPin) {
        this.stepPin = stepPin;
    }

    public Integer getStepsPerCl() {
        return stepsPerCl;
    }

    public void setStepsPerCl(Integer stepsPerCl) {
        this.stepsPerCl = stepsPerCl;
    }

    public Integer getMaxStepsPerSecond() {
        return minStepDeltaInMs;
    }

    public void setMaxStepsPerSecond(Integer minStepDeltaInMs) {
        this.minStepDeltaInMs = minStepDeltaInMs;
    }

    public Integer getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Integer acceleration) {
        this.acceleration = acceleration;
    }

    @Override
    public boolean isRunning() {
        //TODO
        throw new IllegalStateException("Not implemented yet!");
    }

    @Override
    public void setRunning(boolean run) {
        //TODO
        throw new IllegalStateException("Not implemented yet!");
    }

    @Override
    public boolean isCanPump() {
        return this.enablePin != null && this.stepPin != null &&
                this.minStepDeltaInMs != null && this.acceleration != null;
    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted() && this.stepsPerCl != null;
    }
}
