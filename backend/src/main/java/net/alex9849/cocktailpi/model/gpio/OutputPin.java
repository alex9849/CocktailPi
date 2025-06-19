package net.alex9849.cocktailpi.model.gpio;

import lombok.Getter;
import net.alex9849.cocktailpi.exception.PinException;
import net.alex9849.motorlib.pin.IOutputPin;
import net.alex9849.motorlib.pin.PinState;

public class OutputPin implements IOutputPin {
    private final IOutputPin pin;
    @Getter
    private final HardwarePin boardHwPin;

    public OutputPin(IOutputPin pin, HardwarePin boardHwPin) {
        this.pin = pin;
        this.boardHwPin = boardHwPin;
    }


    @Override
    public void digitalWrite(PinState pinState) {
        try {
            this.pin.digitalWrite(pinState);
        } catch (Exception e) {
            throw new PinException(e, this.boardHwPin);
        }
    }

    @Override
    public boolean isHigh() {
        try {
            return this.pin.isHigh();
        } catch (Exception e) {
            throw new PinException(e, this.boardHwPin);
        }
    }

    @Override
    public void digitalWriteAndWait(PinState pinState) {
        try {
            this.pin.digitalWriteAndWait(pinState);
        } catch (Exception e) {
            throw new PinException(e, this.boardHwPin);
        }
    }

    @Override
    public void setWaitAfterWriteTimeNs(long ns) {
        this.pin.setWaitAfterWriteTimeNs(ns);
    }

}
