package net.alex9849.cocktailmaker.iface;

import com.pi4j.io.gpio.digital.DigitalOutput;

public class ProdModeGpioPin implements IGpioPin {
    private DigitalOutput pin;

    ProdModeGpioPin(DigitalOutput pin) {
        this.pin = pin;
    }

    @Override
    public synchronized boolean isHigh() {
        return pin.isHigh();
    }

    @Override
    public synchronized void setHigh() {
        pin.high();
        System.out.println("Pin " + pin.getAddress() + " set to high!");
    }

    @Override
    public synchronized void setLow() {
        pin.low();
        System.out.println("Pin " + pin.getAddress() + " set to low!");
    }
}
