package net.alex9849.cocktailpi.model.gpio;

import net.alex9849.motorlib.pin.IOutputPin;

public class I2CBoardPin extends Pin {

    public I2CBoardPin(I2CGpioBoard board, int nr) {
        super(board, nr);
    }

    @Override
    public IOutputPin getOutputPin() {
        return getGpioBoard().getBoardDriver().getOutputPin((byte) this.getPinNr());
    }

    @Override
    public I2CGpioBoard getGpioBoard() {
        return (I2CGpioBoard) super.getGpioBoard();
    }
}
