package net.alex9849.cocktailmaker.model.gpio;

import net.alex9849.motorlib.pin.IOutputPin;

public abstract class GpioBoard {
    private long id;
    private String name;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract int getMinPin();

    public abstract int getMaxPin();

    public Pin getPin(int pin) {
        if(pin < getMinPin() || pin > getMaxPin()) {
            throw new IllegalArgumentException("Pin out of range! Requested pin: " + pin + ", Pin range: " + getMinPin() + " - " + getMaxPin());
        }
        return getPinUnchecked(pin);
    }

    protected abstract Pin getPinUnchecked(int pin);

    public abstract class Pin {
        private int nr;

        public int getPinNr() {
            return nr;
        }

        public void setNr(int nr) {
            this.nr = nr;
        }

        public abstract IOutputPin getOutputPin();

        public abstract GpioBoard getGpioBoard();

        public long getBoardId() {
            return getGpioBoard().getId();
        }
    }
}
