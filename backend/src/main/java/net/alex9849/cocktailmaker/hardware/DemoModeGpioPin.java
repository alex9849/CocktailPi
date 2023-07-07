package net.alex9849.cocktailmaker.hardware;

import net.alex9849.motorlib.IMotorPin;

public class DemoModeGpioPin implements IMotorPin {
    private int bcmPinNr;
    private boolean isHigh;

    DemoModeGpioPin(int bcmPinNr) {
        this.bcmPinNr = bcmPinNr;
    }

    @Override
    public synchronized void digitalWrite(PinState value) {
        if(value == PinState.HIGH) {
            this.isHigh = true;
            System.out.println("Pin " + bcmPinNr + " set to high!");
        } else {
            this.isHigh = false;
            System.out.println("Pin " + bcmPinNr + " set to low!");
        }
    }

    @Override
    public synchronized boolean isHigh() {
        return this.isHigh;
    }
}
