package net.alex9849.cocktailmaker.utils;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import net.alex9849.motorlib.pin.IOutputPin;
import net.alex9849.motorlib.pin.Pi4JOutputPin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PinUtils {
    @Autowired
    private Context pi4J;
    private final Map<Integer, IOutputPin> outputPinMap = new HashMap<>();

    public synchronized IOutputPin getBoardOutputPin(int address) {
        if(!outputPinMap.containsKey(address)) {
            DigitalOutputProvider provider = pi4J.dout();
            DigitalOutputConfig runCfg = DigitalOutput.newConfigBuilder(pi4J).address(address).build();
            if(!pi4J.registry().exists(runCfg.id())) {
                provider.create(runCfg);
            }
            IOutputPin runPin = new Pi4JOutputPin(pi4J.registry().get(runCfg.id()));
            outputPinMap.put(address, runPin);
        }
        return outputPinMap.get(address);
    }

}
