package net.alex9849.cocktailpi.model.gpio.local;

import com.pi4j.io.gpio.digital.PullResistance;
import net.alex9849.cocktailpi.model.gpio.InputPin;
import net.alex9849.cocktailpi.model.gpio.OutputPin;
import net.alex9849.cocktailpi.model.gpio.HardwarePin;
import net.alex9849.cocktailpi.utils.PinUtils;
import net.alex9849.cocktailpi.utils.SpringUtility;
import net.alex9849.motorlib.pin.IInputPin;
import net.alex9849.motorlib.pin.IOutputPin;

public class LocalHwPin extends HardwarePin {

    public LocalHwPin(LocalGpioBoard board, int nr, String displayName) {
        super(board, nr, displayName);
    }

    @Override
    public OutputPin getOutputPin() {
        PinUtils pinUtils = SpringUtility.getBean(PinUtils.class);
        IOutputPin outPin = pinUtils.getBoardOutputPin(getPinNr());
        return new OutputPin(outPin, this);
    }

    @Override
    public InputPin getInputPin(PullResistance pull) {
        PinUtils pinUtils = SpringUtility.getBean(PinUtils.class);
        IInputPin inPin = pinUtils.getBoardInputPin(getPinNr(), pull);
        return new InputPin(inPin, this);
    }

    @Override
    public LocalGpioBoard getGpioBoard() {
        return (LocalGpioBoard) super.getGpioBoard();
    }

}
