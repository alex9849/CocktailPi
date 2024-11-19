package net.alex9849.cocktailpi.model.gpio;

import com.pi4j.io.gpio.digital.PullResistance;
import net.alex9849.motorlib.pin.IInputPin;
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
    public IInputPin getInputPin(PullResistance pull) {
        throw new IllegalStateException("I2C pins are not supported as InputPins!");
    }

    @Override
    public I2CGpioBoard getGpioBoard() {
        return (I2CGpioBoard) super.getGpioBoard();
    }
}
