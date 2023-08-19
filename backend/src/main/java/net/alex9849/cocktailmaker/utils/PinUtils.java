package net.alex9849.cocktailmaker.utils;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.i2c.I2CProvider;
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
    private final Map<Integer, I2C> i2CMap = new HashMap<>();

    public synchronized IOutputPin getBoardOutputPin(int address) {
        if(!outputPinMap.containsKey(address)) {
            DigitalOutputConfig config = DigitalOutput
                    .newConfigBuilder(pi4J)
                    .address(address)
                    .shutdown(DigitalState.HIGH)
                    .initial(DigitalState.HIGH)
                    .provider("pigpio-digital-output")
                    .build();

            if(!pi4J.registry().exists(config.id())) {
                DigitalOutput pin = pi4J.create(config);
                outputPinMap.put(address, new Pi4JOutputPin(pin));
            }
        }
        return outputPinMap.get(address);
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

}
