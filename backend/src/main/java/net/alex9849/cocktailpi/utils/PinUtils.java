package net.alex9849.cocktailpi.utils;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.i2c.I2CProvider;
import net.alex9849.cocktailpi.model.gpio.HardwarePin;
import net.alex9849.cocktailpi.model.gpio.PinResource;
import net.alex9849.motorlib.pin.IInputPin;
import net.alex9849.motorlib.pin.IOutputPin;
import net.alex9849.motorlib.pin.Pi4JInputPin;
import net.alex9849.motorlib.pin.Pi4JOutputPin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PinUtils {
    @Autowired
    private Context pi4J;

    private final Map<Integer, DigitalOutput> outputPinMap = new HashMap<>();
    private final Map<Integer, DigitalInput> inputPinMap = new HashMap<>();
    private final Map<Integer, I2C> i2CMap = new HashMap<>();

    public synchronized IOutputPin getBoardOutputPin(int address) {
        if(inputPinMap.containsKey(address)) {
            DigitalInput old = inputPinMap.remove(address);
            old.shutdown(pi4J);
            pi4J.registry().remove(old.id());
        }
        if(!outputPinMap.containsKey(address)) {
            DigitalOutputConfig config = DigitalOutput
                    .newConfigBuilder(pi4J)
                    .address(address)
                    .build();

            if(!pi4J.registry().exists(config.id())) {
                DigitalOutput pin = pi4J.create(config);
                outputPinMap.put(address, pin);
            }
        }
        return new Pi4JOutputPin(outputPinMap.get(address));
    }

    public synchronized IInputPin getBoardInputPin(int address, PullResistance pull) {
        if(outputPinMap.containsKey(address)) {
            DigitalOutput old = outputPinMap.remove(address);
            old.shutdown(pi4J);
            pi4J.registry().remove(old.id());
        }
        if(inputPinMap.containsKey(address)) {
            DigitalInput pin = inputPinMap.get(address);
            if(pin.pull() != pull) {
                inputPinMap.remove(address).shutdown(pi4J);
            }
        }
        if(!inputPinMap.containsKey(address)) {
            DigitalInputConfig config = DigitalInput
                    .newConfigBuilder(pi4J)
                    .address(address)
                    .pull(pull)
                    .build();

            if(!pi4J.registry().exists(config.id())) {
                DigitalInput pin = pi4J.create(config);
                inputPinMap.put(address, pin);
            }
        }
        return new Pi4JInputPin(inputPinMap.get(address));
    }

    public synchronized I2C getI2c(int address) {
        if(!i2CMap.containsKey(address)) {
            I2CProvider provider = pi4J.getI2CProvider();
            I2CConfig i2CConfig = I2CConfigBuilder.newInstance(pi4J).bus(1).device(address).build();
            if(!pi4J.registry().exists(i2CConfig.id())) {
                i2CMap.put(address, provider.create(i2CConfig));
            }
        }
        return i2CMap.get(address);
    }

    public synchronized void shutdownOutputPin(int address) {
        if(!outputPinMap.containsKey(address)) {
            return;
        }
        DigitalOutput dout = outputPinMap.remove(address);
        pi4J.shutdown(dout.id());
    }

    public synchronized void shutdownI2CAddress(int address) {
        if(!i2CMap.containsKey(address)) {
            return;
        }
        I2C i2c = i2CMap.remove(address);
        pi4J.shutdown(i2c.id());
    }

    public synchronized void shutdownI2C() {
        i2CMap.values().forEach(x -> pi4J.shutdown(x.id()));
        i2CMap.clear();
        pi4J.getI2CProvider().shutdown(pi4J);
    }

    public static void failIfPinOccupiedOrDoubled(PinResource.Type allowedType, Long allowedIdIfTypeMatch, HardwarePin... hwPins) {
        List<PinResource> pinResources = new ArrayList<>();
        Set<Map.Entry<Long, Integer>> cPins = new HashSet<>();
        boolean pinDoubled = false;
        for(HardwarePin cHwPin : hwPins) {
            if (cHwPin != null) {
                pinDoubled |= !cPins.add(Map.entry(cHwPin.getBoardId(), cHwPin.getPinNr()));
                if(cHwPin.getResource() != null) {
                    pinResources.add(cHwPin.getResource());
                }
            }
        }
        if(pinDoubled) {
            throw new IllegalArgumentException("Pins need to be different!");
        }
        for(PinResource resource : pinResources) {
            if(!(resource.getType() == allowedType && (allowedIdIfTypeMatch == null || resource.getId() == allowedIdIfTypeMatch))) {
                throw new IllegalArgumentException("Pin already in use!");
            }
        }
    }

}
