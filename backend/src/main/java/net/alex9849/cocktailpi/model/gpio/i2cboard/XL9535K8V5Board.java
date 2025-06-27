package net.alex9849.cocktailpi.model.gpio.i2cboard;

public class XL9535K8V5Board extends XL9535Board {

    @Override
    public I2CBoardModel getBoardModel() {
        return I2CBoardModel.XL9535_K8V5_RELAY;
    }
}