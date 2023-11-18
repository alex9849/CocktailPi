package net.alex9849.cocktailpi.model.system.settings;

import net.alex9849.cocktailpi.model.gpio.GpioBoard;

public class I2CSettings {
    private boolean enable;
    private GpioBoard.Pin sdaPin;
    private GpioBoard.Pin sclPin;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public GpioBoard.Pin getSdaPin() {
        return sdaPin;
    }

    public void setSdaPin(GpioBoard.Pin sdaPin) {
        this.sdaPin = sdaPin;
    }

    public GpioBoard.Pin getSclPin() {
        return sclPin;
    }

    public void setSclPin(GpioBoard.Pin sclPin) {
        this.sclPin = sclPin;
    }
}
