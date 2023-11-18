package net.alex9849.cocktailpi.model.pump;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PumpAdvice {
    public enum Type {
        RUN, PUMP_UP, PUMP_DOWN, PUMP_ML, PUMP_TIME, PUMP_STEPS
    }

    @NotNull
    private final Type type;
    @Min(1)
    private final long amount;

    public PumpAdvice(Type type, long amount) {
        this.type = type;
        this.amount = amount;
    }

    public Type getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }
}
