package net.alex9849.cocktailpi.model.gpio;

import java.util.ArrayList;
import java.util.List;

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

    public List<Pin> getPins() {
        List<Pin> pinList = new ArrayList<>();
        for(int i = getMinPin(); i <= getMaxPin(); i++) {
            pinList.add(getPinUnchecked(i));
        }
        return pinList;
    }

}
