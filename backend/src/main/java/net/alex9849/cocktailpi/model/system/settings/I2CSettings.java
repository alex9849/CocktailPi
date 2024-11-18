package net.alex9849.cocktailpi.model.system.settings;

import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.gpio.Pin;

public class I2CSettings {
    private boolean enable;
    private Pin sdaPin;
    private Pin sclPin;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Pin getSdaPin() {
        return sdaPin;
    }

    public void setSdaPin(Pin sdaPin) {
        this.sdaPin = sdaPin;
    }

    public Pin getSclPin() {
        return sclPin;
    }

    public void setSclPin(Pin sclPin) {
        this.sclPin = sclPin;
    }
}
