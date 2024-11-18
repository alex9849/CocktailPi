package net.alex9849.cocktailpi.model.system;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.gpio.Pin;

@Getter @Setter
public class I2cStatus {
    private boolean enabled;
    private Pin sdaPin;
    private Pin sclPin;
}
