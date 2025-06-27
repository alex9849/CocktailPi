package net.alex9849.cocktailpi.model.gpio.i2cboard;

import com.pi4j.io.i2c.I2C;
import net.alex9849.cocktailpi.utils.PinUtils;
import net.alex9849.cocktailpi.utils.SpringUtility;
import net.alex9849.motorlib.pin.I2CPinExpander;
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
        if (pin < 8) {
            return "A" + String.valueOf(pin);
        } else {
            return "B" + String.valueOf(pin % 8);
        }
    }
}
