package net.alex9849.cocktailpi.model.gpio.i2cboard;

public class XL9535K16V5Board extends XL9535Board {

    @Override
    public I2CBoardModel getBoardModel() {
        return I2CBoardModel.XL9535_K16V5_RELAY;
    }
}