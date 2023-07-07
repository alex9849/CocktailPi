package net.alex9849.cocktailmaker.hardware;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import net.alex9849.motorlib.IMotorPin;

import java.util.HashMap;

public class ProdModeGpioController implements IGpioController {

    private final HashMap<Integer, ProdModeGpioPin> pinMap = new HashMap<>();
    public Context context = Pi4J.newAutoContext();

    @Override
    public synchronized IMotorPin getGpioPin(int bcmPinNr) {
        if(!pinMap.containsKey(bcmPinNr)) {
            DigitalOutputConfigBuilder config = DigitalOutput
                    .newConfigBuilder(context)
                    .address(bcmPinNr)
                    .shutdown(DigitalState.HIGH)
                    .initial(DigitalState.HIGH)
                    .provider("pigpio-digital-output");
            DigitalOutput pi4jPin = context.create(config);
            pinMap.put(bcmPinNr, new ProdModeGpioPin(pi4jPin));
        }
        return pinMap.get(bcmPinNr);
    }

    @Override
    public synchronized void shutdown() {
        context.shutdown();
    }
}
