package net.alex9849.cocktailmaker.iface;

import java.util.HashMap;

public class DemomodeGpioController implements IGpioController {
    private HashMap<Integer, DemoModeGpioPin> pinMap = new HashMap<>();

    @Override
    public synchronized IGpioPin getGpioPin(int bcmPinNr) {
        return pinMap.computeIfAbsent(bcmPinNr, p -> new DemoModeGpioPin(bcmPinNr));
    }

    @Override
    public void shutdown() {
        //Do nothing
    }
}
