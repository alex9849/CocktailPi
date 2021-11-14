package net.alex9849.cocktailmaker.iface;

import com.pi4j.io.gpio.Pin;

import java.util.HashMap;

public class DemomodeGpioController implements IGpioController {
    private HashMap<Pin, DemoModeGpioPin> pinMap = new HashMap<>();

    @Override
    public synchronized IGpioPin provideGpioPin(Pin pin) {
        return pinMap.computeIfAbsent(pin, p -> new DemoModeGpioPin(pin));
    }

    @Override
    public void shutdown() {
        //Do nothing
    }
}
