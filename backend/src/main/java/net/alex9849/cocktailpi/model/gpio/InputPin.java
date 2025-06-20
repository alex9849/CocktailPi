package net.alex9849.cocktailpi.model.gpio;

import lombok.Getter;
import net.alex9849.cocktailpi.model.system.ErrorInfo;
import net.alex9849.motorlib.pin.IInputPin;

public class InputPin implements IInputPin {
    private final IInputPin pin;
    @Getter
    private final HardwarePin boardHwPin;

    public InputPin(IInputPin pin, HardwarePin boardHwPin) {
        this.pin = pin;
        this.boardHwPin = boardHwPin;
    }

    @Override
    public boolean isHigh() {
        try {
            return this.pin.isHigh();
        } catch (Exception e) {
            boardHwPin.getGpioBoard().addError(new ErrorInfo(e));
            throw e;
        }
    }

    @Override
    public boolean isPull() {
        try {
            return this.pin.isPull();
        } catch (Exception e) {
            boardHwPin.getGpioBoard().addError(new ErrorInfo(e));
            throw e;
        }
    }
}
