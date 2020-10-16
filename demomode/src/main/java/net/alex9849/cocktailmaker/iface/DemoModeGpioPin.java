package net.alex9849.cocktailmaker.iface;

public class DemoModeGpioPin implements IGpioPin {
    private boolean isHigh;

    @Override
    public boolean isHigh() {
        return this.isHigh;
    }

    @Override
    public void setHigh() {
        this.isHigh = true;
    }

    @Override
    public void setLow() {
        this.isHigh = false;
    }
}
