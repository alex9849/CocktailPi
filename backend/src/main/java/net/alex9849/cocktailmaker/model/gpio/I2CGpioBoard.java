package net.alex9849.cocktailmaker.model.gpio;


import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailmaker.utils.PinUtils;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import net.alex9849.motorlib.mcp230xx.Mcp23017;
import net.alex9849.motorlib.pin.IOutputPin;

import java.util.HashMap;
import java.util.Map;

@DiscriminatorValue("i2c")
public class I2CGpioBoard extends GpioBoard {
    private static final Map<Byte, Mcp23017> boardMap = new HashMap<>();
    private final SubType subType;
    private byte i2cAddress;

    public I2CGpioBoard(SubType subType) {
        this.subType = subType;
    }

    public byte getI2cAddress() {
        return i2cAddress;
    }

    public void setI2cAddress(byte i2cAddress) {
        this.i2cAddress = i2cAddress;
    }

    public SubType getSubType() {
        return subType;
    }

    public Mcp23017 getBoardDriver() {
        if(!boardMap.containsKey(getI2cAddress())) {
            PinUtils pinUtils = SpringUtility.getBean(PinUtils.class);
            boardMap.put(getI2cAddress(), new Mcp23017(pinUtils.getI2c(getI2cAddress())));
        }
        return boardMap.get(getI2cAddress());
    }

    @Override
    protected GpioBoard.Pin getPinUnchecked(int pin) {
        Pin oPin = new Pin();
        oPin.setNr(pin);
        return oPin;
    }

    @Override
    public int getMinPin() {
        return subType.minPin;
    }

    @Override
    public int getMaxPin() {
        return subType.maxPin;
    }

    public enum SubType {
        MCP23017(0, 15);

        final int minPin;
        final int maxPin;

        SubType(int minPin, int maxPin) {
            this.minPin = minPin;
            this.maxPin = maxPin;
        }
    }

    public class Pin extends GpioBoard.Pin {

        @Override
        public IOutputPin getOutputPin() {
            return I2CGpioBoard.this.getBoardDriver().getOutputPin((byte) this.getPinNr());
        }

        @Override
        public I2CGpioBoard getGpioBoard() {
            return I2CGpioBoard.this;
        }

    }

}
