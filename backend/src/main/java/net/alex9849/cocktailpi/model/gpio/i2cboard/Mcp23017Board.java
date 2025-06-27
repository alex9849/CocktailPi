package net.alex9849.cocktailpi.model.gpio.i2cboard;

import com.pi4j.io.i2c.I2C;
import net.alex9849.motorlib.mcp230xx.Mcp23017;
import net.alex9849.motorlib.pin.I2CPinExpander;


public class Mcp23017Board extends I2CGpioBoard {

    public I2CBoardModel getBoardModel() {
        return I2CBoardModel.MCP23017;
    }

    @Override
    protected boolean isExpanderInstance(I2CPinExpander expander) {
        return expander instanceof Mcp23017;
    }

    @Override
    protected I2CPinExpander genExpanderInstance(I2C device) {
        return new Mcp23017(device);
    }

    @Override
    protected String pinDisplayName(int pin) {
        if (pin < 8) {
            return "PA" + String.valueOf(pin);
        } else {
            return "PB" + String.valueOf(pin % 8);
        }
    }
}
