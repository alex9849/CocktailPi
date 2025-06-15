package net.alex9849.cocktailpi.model.gpio;

import com.pi4j.io.i2c.I2C;
import net.alex9849.motorlib.I2CPinExpander;
import net.alex9849.motorlib.xl9535.XL9535;


public abstract class XL9535Board extends I2CGpioBoard {

    @Override
    protected boolean isExpanderInstance(I2CPinExpander expander) {
        return expander instanceof XL9535;
    }

    @Override
    protected I2CPinExpander genExpanderInstance(I2C device) {
        return new XL9535(device);
    }

    @Override
    protected String pinDisplayName(int pin) {
        return "A" + String.valueOf(pin);
    }
}
