package net.alex9849.cocktailmaker.iface;

public interface IGpioController {

    IGpioPin getGpioPin(int bcmPinNr);

    void shutdown();

}
