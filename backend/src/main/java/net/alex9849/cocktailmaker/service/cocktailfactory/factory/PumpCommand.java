package net.alex9849.cocktailmaker.service.cocktailfactory.factory;

import net.alex9849.cocktailmaker.model.Pump;

public class PumpCommand {
    private Pump pump;
    private int time;
    private PumpStatus status;

    public PumpCommand(Pump pump, int time, PumpStatus status) {
        this.pump = pump;
        this.time = time;
        this.status = status;
    }

    public Pump getPump() {
        return pump;
    }

    public int getTime() {
        return time;
    }

    public PumpStatus getStatus() {
        return status;
    }
}
