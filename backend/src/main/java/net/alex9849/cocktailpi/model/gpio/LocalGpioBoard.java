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
    protected GpioBoard.Pin getPinUnchecked(int pin) {
        Pin oPin = new Pin(pin);
        return oPin;
    }


    public class Pin extends GpioBoard.Pin {

        public Pin(int nr) {
            super(nr);
        }

        @Override
        public IOutputPin getOutputPin() {
            PinUtils pinUtils = SpringUtility.getBean(PinUtils.class);
            return pinUtils.getBoardOutputPin(getPinNr());
        }

        @Override
        public LocalGpioBoard getGpioBoard() {
            return LocalGpioBoard.this;
        }

    }
}
