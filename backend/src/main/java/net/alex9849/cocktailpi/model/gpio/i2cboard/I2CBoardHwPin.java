package net.alex9849.cocktailpi.model.gpio.i2cboard;

import com.pi4j.io.gpio.digital.PullResistance;
import net.alex9849.cocktailpi.model.gpio.InputPin;
import net.alex9849.cocktailpi.model.gpio.OutputPin;
import net.alex9849.cocktailpi.model.gpio.HardwarePin;
import net.alex9849.motorlib.pin.IOutputPin;

public class I2CBoardHwPin extends HardwarePin {

    public I2CBoardHwPin(I2CGpioBoard board, int nr, String displayName) {
        super(board, nr, displayName);
    }

    @Override
    public OutputPin getOutputPin() {
        IOutputPin pin = getGpioBoard().getBoardDriver().getOutputPin((byte) this.getPinNr());
        return new OutputPin(pin, this);
    }

    @Override
    public InputPin getInputPin(PullResistance pull) {
        throw new IllegalStateException("I2C pins are not supported as InputPins!");
    }

    @Override
    public I2CGpioBoard getGpioBoard() {
        return (I2CGpioBoard) super.getGpioBoard();
    }
}
