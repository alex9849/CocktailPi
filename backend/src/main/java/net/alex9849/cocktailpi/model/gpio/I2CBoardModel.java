package net.alex9849.cocktailpi.model.gpio;

public enum I2CBoardModel {
    MCP23017(0, 15),
    XL9535_K1V5_RELAY(0, 0),
    XL9535_K2V5_RELAY(0, 2),
    XL9535_K4V5_RELAY(0, 4),
    XL9535_K8V5_RELAY(0, 8),
    XL9535_K16V5_RELAY(0, 16);

    final int minPin;
    final int maxPin;

    I2CBoardModel(int minPin, int maxPin) {
        this.minPin = minPin;
        this.maxPin = maxPin;
    }
}
