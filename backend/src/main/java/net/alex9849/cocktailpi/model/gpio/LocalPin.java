package net.alex9849.cocktailpi.model.gpio;

import net.alex9849.cocktailpi.utils.PinUtils;
import net.alex9849.cocktailpi.utils.SpringUtility;
import net.alex9849.motorlib.pin.IOutputPin;

public class LocalPin extends Pin {

    public LocalPin(LocalGpioBoard board, int nr) {
        super(board, nr);
    }

    @Override
    public IOutputPin getOutputPin() {
        PinUtils pinUtils = SpringUtility.getBean(PinUtils.class);
        return pinUtils.getBoardOutputPin(getPinNr());
    }

    @Override
    public LocalGpioBoard getGpioBoard() {
        return (LocalGpioBoard) super.getGpioBoard();
    }

}
