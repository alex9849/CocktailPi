package net.alex9849.cocktailmaker.service.cocktailfactory.factory;

import net.alex9849.cocktailmaker.model.Pump;

import java.util.Objects;

public class PumpPhase {
    private int startTime;
    private int stopTime;
    private final Pump pump;

    public PumpPhase(int startTime, int stopTime, Pump pump) {
        Objects.requireNonNull(pump);
        if(startTime >= stopTime) {
            throw new IllegalArgumentException("stoptime needs to be larger than starttime!");
        }
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.pump = pump;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getStopTime() {
        return stopTime;
    }

    public Pump getPump() {
        return pump;
    }

    public int runTime() {
        return stopTime - startTime;
    }

    public void addTime(int time) {
        this.stopTime += time;
    }
}
