package net.alex9849.cocktailpi.model.gpio;


import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailpi.utils.PinUtils;
import net.alex9849.cocktailpi.utils.SpringUtility;
import net.alex9849.motorlib.mcp230xx.Mcp23017;
import net.alex9849.motorlib.pin.IOutputPin;

import java.util.HashMap;
import java.util.Map;

@DiscriminatorValue("i2c")
public class I2CGpioBoard extends GpioBoard {
    private static final Map<Byte, Mcp23017> boardMap = new HashMap<>();
    private final BoardModel boardModel;
    private byte i2cAddress;

    public I2CGpioBoard(BoardModel boardModel) {
        this.boardModel = boardModel;
    }

    public byte getI2cAddress() {
        return i2cAddress;
    }

    public void setI2cAddress(byte i2cAddress) {
        this.i2cAddress = i2cAddress;
    }

    public BoardModel getBoardModel() {
        return boardModel;
    }

    public Mcp23017 getBoardDriver() {
        if(boardMap.containsKey(getI2cAddress()) && !boardMap.get(getI2cAddress()).isOpen()) {
            boardMap.remove(getI2cAddress());
        }
        if(!boardMap.containsKey(getI2cAddress())) {
            PinUtils pinUtils = SpringUtility.getBean(PinUtils.class);
            boardMap.put(getI2cAddress(), new Mcp23017(pinUtils.getI2c(getI2cAddress())));
        }
        return boardMap.get(getI2cAddress());
    }

    @Override
    protected Pin getPinUnchecked(int pin) {
        return new I2CBoardPin(this, pin);
    }

    @Override
    public int getMinPin() {
        return boardModel.minPin;
    }

    @Override
    public int getMaxPin() {
        return boardModel.maxPin;
    }

    public enum BoardModel {
        MCP23017(0, 15);

        final int minPin;
        final int maxPin;

        BoardModel(int minPin, int maxPin) {
            this.minPin = minPin;
            this.maxPin = maxPin;
        }
    }

}
