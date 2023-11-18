package net.alex9849.cocktailpi.service.pumps.cocktailfactory;

import net.alex9849.cocktailpi.model.pump.DcPump;

import java.util.Objects;

public class PumpPhase {
    private final int startTime;
    private int stopTime;
    private final DcPump pump;
    private Long startedTime;
    private Long stoppedTime;

    public PumpPhase(int startTime, int stopTime, DcPump pump) {
        Objects.requireNonNull(pump);
        if(startTime >= stopTime) {
            throw new IllegalArgumentException("stoptime needs to be larger than starttime!");
        }
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.pump = pump;
    }

    public State getState() {
        if(this.startedTime == null) {
            return State.NOT_STARTED;
        } else if (this.stoppedTime == null) {
            return State.RUNNING;
        } else {
            return State.FINISHED;
        }
    }

    public Long getStoppedTime() {
        return stoppedTime;
    }

    public void setStoppedTime(Long stoppedTime) {
        this.stoppedTime = stoppedTime;
    }

    public Long getStartedTime() {
        return startedTime;
    }

    public void setStarted() {
        if(this.startedTime == null) {
            this.startedTime = System.currentTimeMillis();
        }
    }

    public void setStopped() {
        if(this.startedTime == null) {
            throw new IllegalStateException("PumpPhase setStarted method call need to be done before setStopped call!");
        }
        if(this.stoppedTime == null) {
            this.stoppedTime = System.currentTimeMillis();
        }
    }

    public double getLiquidPumped() {
        if(getStartedTime() == null) {
            return 0;
        }
        Long stoppedTime = getStoppedTime();
        if(stoppedTime == null) {
            stoppedTime = System.currentTimeMillis();
        }
        int runTimeElapsed = (int) (stoppedTime - getStartedTime());
        int runTimeRequested = this.stopTime - this.startTime;

        if(Math.abs(runTimeRequested - runTimeElapsed) < 100) {
            return getLiquidToPump();
        }

        double liquidPumped = pump.getConvertRuntimeToMl(runTimeElapsed);
        liquidPumped = Math.min(liquidPumped, getLiquidToPump());
        return liquidPumped;
    }

    public double getRemainingLiquidToPump() {
        return Math.max(0, getLiquidToPump() - getLiquidPumped());
    }

    public double getLiquidToPump() {
        return pump.getConvertRuntimeToMl(this.stopTime - this.startTime);
    }

    public int getStartTime() {
        return startTime;
    }

    public int getStopTime() {
        return stopTime;
    }

    public DcPump getPump() {
        return pump;
    }

    public int runTime() {
        return stopTime - startTime;
    }

    public void addTime(int time) {
        if(time < 0) {
            throw new IllegalArgumentException("time needs to be positive!");
        }
        this.stopTime += time;
    }


    public enum State {
        NOT_STARTED, FINISHED, RUNNING
    }
}
