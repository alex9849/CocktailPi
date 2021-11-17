package net.alex9849.cocktailmaker.iface;

public class DemoModeGpioPin implements IGpioPin {
    private int bcmPinNr;
    private boolean isHigh;

    DemoModeGpioPin(int bcmPinNr) {
        this.bcmPinNr = bcmPinNr;
    }

    @Override
    public synchronized boolean isHigh() {
        return this.isHigh;
    }

    @Override
    public synchronized void setHigh() {
        this.isHigh = true;
        System.out.println("Pin " + bcmPinNr + " stopped!");
    }

    @Override
    public synchronized void setLow() {
        this.isHigh = false;
        System.out.println("Pin " + bcmPinNr + " started!");
    }
}
