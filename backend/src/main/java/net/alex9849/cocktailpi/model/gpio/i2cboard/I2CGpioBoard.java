package net.alex9849.cocktailpi.model.gpio.i2cboard;


import com.pi4j.io.i2c.I2C;
import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.gpio.GpioBoardType;
import net.alex9849.cocktailpi.model.gpio.HardwarePin;
import net.alex9849.cocktailpi.utils.PinUtils;
import net.alex9849.cocktailpi.utils.SpringUtility;
import net.alex9849.motorlib.pin.I2CPinExpander;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    protected HardwarePin getPinUnchecked(int pin) {
        return new I2CBoardHwPin(this, pin, pinDisplayName(pin));
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof I2CGpioBoard that)) return false;
        if (!super.equals(o)) return false;
        return i2cAddress == that.i2cAddress
                && getBoardModel() == that.getBoardModel();
    }

    public void restart() {
        PinUtils pinUtils = SpringUtility.getBean(PinUtils.class);
        pinUtils.shutdownI2CAddress(getI2cAddress());
        this.getBoardDriver().updateI2c(pinUtils.getI2c(getI2cAddress()), false);
    }

    public void shutdownDriver() {
        if(boardMap.containsKey(getI2cAddress())) {
            I2CPinExpander expander = boardMap.get(getI2cAddress());
            if (expander.isOpen()) {
                PinUtils pinUtils = SpringUtility.getBean(PinUtils.class);
                pinUtils.shutdownI2CAddress(getI2cAddress());
                boardMap.remove(getI2cAddress());
            }
        }
    }
}
