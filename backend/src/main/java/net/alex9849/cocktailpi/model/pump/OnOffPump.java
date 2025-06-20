package net.alex9849.cocktailpi.model.pump;

import net.alex9849.cocktailpi.model.gpio.HardwarePin;

import java.util.Objects;

public abstract class OnOffPump extends Pump {
    private HardwarePin hwPin;
    private Boolean isPowerStateHigh;

    public HardwarePin getPin() {
        return hwPin;
    }

    public void setPin(HardwarePin hwPin) {
        this.hwPin = hwPin;
    }

    public Boolean isPowerStateHigh() {
        return isPowerStateHigh;
    }

    public void setIsPowerStateHigh(Boolean isPowerStateHigh) {
        this.isPowerStateHigh = isPowerStateHigh;
    }

    @Override
    protected boolean isHwPinsCompleted() {
        return this.hwPin != null && this.isPowerStateHigh != null;
    }

    public boolean equalDriverProperties(Pump other) {
        if (other instanceof OnOffPump oopOther) {
            return Objects.equals(hwPin, oopOther.hwPin)
                    &&  Objects.equals(isPowerStateHigh, oopOther.isPowerStateHigh);
        }
        return false;
    }
}
