package net.alex9849.cocktailmaker.hardware;

import net.alex9849.motorlib.pin.IOutputPin;

import java.util.HashMap;

public class DemoModeGpioController implements IGpioController {
    private HashMap<Integer, DemoModeGpioPin> pinMap = new HashMap<>();

    @Override
    public synchronized IOutputPin getGpioPin(int bcmPinNr) {
        return pinMap.computeIfAbsent(bcmPinNr, p -> new DemoModeGpioPin(bcmPinNr));
    }

    @Override
    public void shutdown() {
        //Do nothing
    }
}
