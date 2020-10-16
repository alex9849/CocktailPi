package net.alex9849.cocktailmaker.iface;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class ProdModeGpioPin implements IGpioPin {
    private GpioPinDigitalOutput gpioPin;

    ProdModeGpioPin(GpioPinDigitalOutput gpioPin) {
        this.gpioPin = gpioPin;
    }

    @Override
    public boolean isHigh() {
        return this.gpioPin.isHigh();
    }

    @Override
    public void setHigh() {
        this.gpioPin.high();
    }

    @Override
    public void setLow() {
        this.gpioPin.low();
    }
}
