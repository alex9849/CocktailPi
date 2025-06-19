package net.alex9849.cocktailpi.exception;

import lombok.Getter;
import net.alex9849.cocktailpi.model.gpio.HardwarePin;

public class PinException extends RuntimeException {
    @Getter
    private final HardwarePin hwPin;

    public PinException(String message, Exception e, HardwarePin hwPin) {
        super(message, e);
        this.hwPin = hwPin;
    }

    public PinException(Exception e, HardwarePin hwPin) {
        super(e.getMessage(), e);
        this.hwPin = hwPin;
    }

}
