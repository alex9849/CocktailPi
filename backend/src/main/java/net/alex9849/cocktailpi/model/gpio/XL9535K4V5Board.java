package net.alex9849.cocktailpi.model.gpio;

public class XL9535K4V5Board extends XL9535Board {

    @Override
    public I2CBoardModel getBoardModel() {
        return I2CBoardModel.XL9535_K4V5_RELAY;
    }
}