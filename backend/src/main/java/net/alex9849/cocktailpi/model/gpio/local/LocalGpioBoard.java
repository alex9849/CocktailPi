package net.alex9849.cocktailpi.model.gpio.local;

import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.gpio.GpioBoardType;

public class LocalGpioBoard extends GpioBoard {
    @Override
    public int getMinPin() {
        return 0;
    }

    @Override
    public int getMaxPin() {
        return 27;
    }

    @Override
    protected LocalHwPin getPinUnchecked(int pin) {
        return new LocalHwPin(this, pin, pinDisplayName(pin));
    }

    @Override
    protected String pinDisplayName(int pin) {
        return "BCM" + String.valueOf(pin);
    }

    @Override
    public GpioBoardType getType() {
        return GpioBoardType.LOCAL;
    }

}
