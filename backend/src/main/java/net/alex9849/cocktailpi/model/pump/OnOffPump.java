package net.alex9849.cocktailpi.model.pump;

import net.alex9849.cocktailpi.model.gpio.HardwarePin;

public abstract class OnOffPump extends Pump {
    private HardwarePin hwPin;
    private Boolean isPowerStateHigh;

    public HardwarePin getPin() {
        return hwPin;
    }

    public void setPin(HardwarePin hwPin) {
        this.hwPin = hwPin;
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
        return this.hwPin != null && this.isPowerStateHigh != null;
    }
}
