package net.alex9849.cocktailpi.model.gpio;

import net.alex9849.cocktailpi.model.system.ErrorInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class GpioBoard {
    private long id;
    private String name;
    private List<ErrorInfo> errors = new ArrayList<>();

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

    public List<ErrorInfo> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorInfo> errors) {
        this.errors = errors;
    }

    public Pin getPin(int pin) {
        if(pin < getMinPin() || pin > getMaxPin()) {
            throw new IllegalArgumentException("Pin out of range! Requested pin: " + pin + ", Pin range: " + getMinPin() + " - " + getMaxPin());
        }
        return getPinUnchecked(pin);
    }

    protected abstract Pin getPinUnchecked(int pin);

    protected abstract String pinDisplayName(int pin);

    public List<Pin> getPins() {
        List<Pin> pinList = new ArrayList<>();
        for(int i = getMinPin(); i <= getMaxPin(); i++) {
            pinList.add(getPinUnchecked(i));
        }
        return pinList;
    }

    public abstract GpioBoardType getType();

    public boolean isExceptional() {
        return !this.errors.isEmpty();
    }

}
