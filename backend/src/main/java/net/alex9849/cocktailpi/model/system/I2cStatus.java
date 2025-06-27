package net.alex9849.cocktailpi.model.system;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailpi.model.gpio.HardwarePin;

@Getter @Setter
public class I2cStatus {
    private boolean enabled;
    private HardwarePin sdaHwPin;
    private HardwarePin sclHwPin;
}
