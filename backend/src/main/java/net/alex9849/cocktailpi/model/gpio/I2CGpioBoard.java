package net.alex9849.cocktailpi.model.gpio;


import com.pi4j.io.i2c.I2C;
import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailpi.utils.PinUtils;
import net.alex9849.cocktailpi.utils.SpringUtility;
import net.alex9849.motorlib.pin.I2CPinExpander;

import java.util.HashMap;
import java.util.Map;

public abstract class I2CGpioBoard extends GpioBoard {
    private static final Map<Byte, I2CPinExpander> boardMap = new HashMap<>();
    private byte i2cAddress;

    public byte getI2cAddress() {
        return i2cAddress;
    }

    public void setI2cAddress(byte i2cAddress) {
        this.i2cAddress = i2cAddress;
    }

    public abstract I2CBoardModel getBoardModel();

    public I2CPinExpander getBoardDriver() {
        PinUtils pinUtils = SpringUtility.getBean(PinUtils.class);
        if(boardMap.containsKey(getI2cAddress())) {
            I2CPinExpander expander = boardMap.get(getI2cAddress());
            if (!expander.isOpen() || !isExpanderInstance(expander)) {
                if (expander.isOpen()) {
                    pinUtils.shutdownI2CAddress(getI2cAddress());
                }
                boardMap.remove(getI2cAddress());
            }
        }
        if(!boardMap.containsKey(getI2cAddress())) {
            boardMap.put(getI2cAddress(), genExpanderInstance(pinUtils.getI2c(getI2cAddress())));
        }
        return boardMap.get(getI2cAddress());
    }

    protected abstract boolean isExpanderInstance(I2CPinExpander expander);

    protected abstract I2CPinExpander genExpanderInstance(I2C device);

    @Override
    protected Pin getPinUnchecked(int pin) {
        return new I2CBoardPin(this, pin, pinDisplayName(pin));
    }

    @Override
    public int getMinPin() {
        return getBoardModel().minPin;
    }

    @Override
    public int getMaxPin() {
        return getBoardModel().maxPin;
    }

    @Override
    public GpioBoardType getType() {
        return GpioBoardType.I2C;
    }
}
