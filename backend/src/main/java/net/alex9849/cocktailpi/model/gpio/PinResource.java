package net.alex9849.cocktailpi.model.gpio;

public class PinResource {
    private long id;
    private Type type;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Type {
        PUMP, I2C, PUMP_DIRECTION, GPIO_BOARD, LOAD_CELL
    }
}
