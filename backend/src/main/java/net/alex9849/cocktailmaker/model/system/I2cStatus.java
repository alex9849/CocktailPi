package net.alex9849.cocktailmaker.model.system;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailmaker.model.gpio.GpioBoard;

@Getter @Setter
public class I2cStatus {
    private boolean enabled;
    private GpioBoard.Pin sdaPin;
    private GpioBoard.Pin sclPin;
}
