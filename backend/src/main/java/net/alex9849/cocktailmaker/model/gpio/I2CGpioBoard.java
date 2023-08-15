package net.alex9849.cocktailmaker.model.gpio;


import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailmaker.utils.PinUtils;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import net.alex9849.motorlib.mcp230xx.Mcp23017;
import net.alex9849.motorlib.pin.IOutputPin;

@DiscriminatorValue("i2c")
public class I2CGpioBoard extends GpioBoard {
    private final SubType subType;
    private byte i2cAddress;
    private Mcp23017 boardDriver;

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

    public IOutputPin providePin(int pin) {
        if(pin < subType.minPin || pin > getSubType().maxPin) {
            throw new IllegalArgumentException("Pin out of range! Requested pin: " + pin + ", Pin range: " + subType.minPin + " - " + subType.maxPin);
        }
        if(boardDriver == null) {
            PinUtils pinUtils = SpringUtility.getBean(PinUtils.class);
            boardDriver = new Mcp23017(pinUtils.getI2c(getI2cAddress()));
        }
        return boardDriver.getOutputPin((byte) pin);
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

}
