package net.alex9849.cocktailpi.model.pump;

import net.alex9849.motorlib.motor.IMotor;
import net.alex9849.motorlib.pin.IOutputPin;
import net.alex9849.motorlib.pin.PinState;

import java.util.Objects;

public class ValveDriver implements IMotor {
    private final PinState runningState;
    private final IOutputPin runPin;
    private Boolean running;


    public ValveDriver(IOutputPin runPin, PinState runningState) {
        this.runningState = runningState;
        this.runPin = runPin;
        this.running = null;
    }

    public synchronized void setOpen(boolean open) {
        running = open;
        if (running) {
            this.runPin.digitalWrite(this.runningState);
        } else if (this.runningState == PinState.HIGH) {
            this.runPin.digitalWrite(PinState.LOW);
        } else {
            this.runPin.digitalWrite(PinState.HIGH);
        }
    }

    public synchronized boolean isOpen() {
        if (this.running == null) {
            this.running = this.runPin.isHigh() && this.runningState == PinState.HIGH;
        }
        return this.running;
    }

    @Override
    public synchronized void shutdown() {
        setOpen(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValveDriver that = (ValveDriver) o;
        return runningState == that.runningState && Objects.equals(runPin, that.runPin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runningState, runPin);
    }
}
