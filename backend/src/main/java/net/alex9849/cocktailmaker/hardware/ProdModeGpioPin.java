package net.alex9849.cocktailmaker.hardware;

import com.pi4j.io.gpio.digital.DigitalOutput;
import net.alex9849.motorlib.pin.IOutputPin;
import net.alex9849.motorlib.pin.PinState;

public class ProdModeGpioPin implements IOutputPin {
    private DigitalOutput pin;

    ProdModeGpioPin(DigitalOutput pin) {
        this.pin = pin;
    }

    @Override
    public synchronized void digitalWrite(PinState value) {
        if(value == PinState.HIGH) {
            pin.high();
        } else {
            pin.low();
        }
    }

    @Override
    public synchronized boolean isHigh() {
        return pin.isHigh();
    }

}
