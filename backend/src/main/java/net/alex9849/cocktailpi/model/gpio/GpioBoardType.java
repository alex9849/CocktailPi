package net.alex9849.cocktailpi.model.gpio;

public enum GpioBoardType {
    LOCAL("local"), I2C("i2c");

    public final String discriminatorValue;
    GpioBoardType(String discriminatorValue) {
        this.discriminatorValue = discriminatorValue;
    }
}
