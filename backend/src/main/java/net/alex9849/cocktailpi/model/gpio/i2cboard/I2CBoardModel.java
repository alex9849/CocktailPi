package net.alex9849.cocktailpi.model.gpio.i2cboard;

public enum I2CBoardModel {
    MCP23017(0, 15),
    XL9535_K1V5_RELAY(0, 0),
    XL9535_K2V5_RELAY(0, 1),
    XL9535_K4V5_RELAY(0, 3),
    XL9535_K8V5_RELAY(0, 7),
    XL9535_K16V5_RELAY(0, 15);

    final int minPin;
    final int maxPin;

    I2CBoardModel(int minPin, int maxPin) {
        this.minPin = minPin;
        this.maxPin = maxPin;
    }

    public static I2CGpioBoard genInstance(I2CBoardModel model) {
        I2CGpioBoard i2CGpioBoard;
        switch (model) {
            case MCP23017 -> i2CGpioBoard = new Mcp23017Board();
            case XL9535_K1V5_RELAY -> i2CGpioBoard = new XL9535K1V5Board();
            case XL9535_K2V5_RELAY -> i2CGpioBoard = new XL9535K2V5Board();
            case XL9535_K4V5_RELAY -> i2CGpioBoard = new XL9535K4V5Board();
            case XL9535_K8V5_RELAY -> i2CGpioBoard = new XL9535K8V5Board();
            case XL9535_K16V5_RELAY -> i2CGpioBoard = new XL9535K16V5Board();
            default -> throw new IllegalArgumentException("Unknown i2c-board model: " + model);
        }
        return i2CGpioBoard;
    }
}
