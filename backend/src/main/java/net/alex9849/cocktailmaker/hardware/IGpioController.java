package net.alex9849.cocktailmaker.hardware;

import net.alex9849.motorlib.pin.IOutputPin;

public interface IGpioController {

    IOutputPin getGpioPin(int bcmPinNr);

    void shutdown();

}
