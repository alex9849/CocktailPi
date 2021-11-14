package net.alex9849.cocktailmaker.iface;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;

import java.util.HashMap;

public class ProdModeGpioController implements IGpioController {

    public GpioController controller = GpioFactory.getInstance();
    private HashMap<Pin, ProdModeGpioPin> pinMap = new HashMap<>();

    @Override
    public synchronized IGpioPin provideGpioPin(Pin pin) {
        return pinMap.computeIfAbsent(pin, (p) -> new ProdModeGpioPin(controller.provisionDigitalOutputPin(p)));
    }

    @Override
    public synchronized void shutdown() {
        controller.shutdown();
    }
}
