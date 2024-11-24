package net.alex9849.cocktailpi.model.pump;

import net.alex9849.cocktailpi.model.gpio.Pin;

public abstract class OnOffPump extends Pump {
    private Pin pin;
    private Boolean isPowerStateHigh;

    public Pin getPin() {
        return pin;
    }

    public void setPin(Pin pin) {
        this.pin = pin;
        this.shutdownDriver();
    }

    public Boolean isPowerStateHigh() {
        return isPowerStateHigh;
    }

    public void setIsPowerStateHigh(Boolean isPowerStateHigh) {
        this.isPowerStateHigh = isPowerStateHigh;
        this.shutdownDriver();
    }

    @Override
    protected boolean isHwPinsCompleted() {
        return this.pin != null && this.isPowerStateHigh != null;
    }
}
