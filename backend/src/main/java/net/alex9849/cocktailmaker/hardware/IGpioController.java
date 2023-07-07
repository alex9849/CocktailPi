package net.alex9849.cocktailmaker.hardware;

import net.alex9849.motorlib.IMotorPin;

public interface IGpioController {

    IMotorPin getGpioPin(int bcmPinNr);

    void shutdown();

}
