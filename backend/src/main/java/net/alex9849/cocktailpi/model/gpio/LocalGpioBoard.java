package net.alex9849.cocktailpi.model.gpio;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailpi.utils.PinUtils;
import net.alex9849.cocktailpi.utils.SpringUtility;
import net.alex9849.motorlib.pin.IOutputPin;

@DiscriminatorValue("local")
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
    protected LocalPin getPinUnchecked(int pin) {
        return new LocalPin(this, pin);
    }

}
