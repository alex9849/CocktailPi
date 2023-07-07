package net.alex9849.cocktailmaker.hardware;

import com.pi4j.io.gpio.digital.DigitalOutput;
import net.alex9849.motorlib.IMotorPin;

public class ProdModeGpioPin implements IMotorPin {
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
