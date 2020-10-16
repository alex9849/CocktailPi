package net.alex9849.cocktailmaker.iface;

import com.pi4j.io.gpio.Pin;

public interface IGpioController {

    IGpioPin provideGpioPin(Pin pin);

    void shutdown();

}
