package net.alex9849.cocktailpi.model.system.settings;

import net.alex9849.cocktailpi.model.gpio.HardwarePin;

public class I2CSettings {
    private boolean enable;
    private HardwarePin sdaHwPin;
    private HardwarePin sclHwPin;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public HardwarePin getSdaPin() {
        return sdaHwPin;
    }

    public void setSdaPin(HardwarePin sdaHwPin) {
        this.sdaHwPin = sdaHwPin;
    }

    public HardwarePin getSclPin() {
        return sclHwPin;
    }

    public void setSclPin(HardwarePin sclHwPin) {
        this.sclHwPin = sclHwPin;
    }
}
