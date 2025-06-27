package net.alex9849.cocktailpi.model.pump.motortasks;

import net.alex9849.cocktailpi.model.pump.*;
import net.alex9849.motorlib.exception.HX711Exception;
import net.alex9849.motorlib.motor.Direction;
import net.alex9849.motorlib.sensor.HX711;

public class ValveTask extends PumpTask {
    private Valve valve;
    private long goalGrams;
    private long initialReadGrams;
    private long currentGrams;

    public ValveTask(Valve valve, long goalGrams, Long prevJobId, Pump pump,
                     boolean isPumpUp, Runnable callback) {
        super(prevJobId, pump, goalGrams == Long.MAX_VALUE, isPumpUp, Direction.FORWARD, callback);
        this.valve = valve;
        this.goalGrams = goalGrams;
        this.currentGrams = 0;
        this.initialReadGrams = 0;
    }

    @Override
    protected void pumpRun() {
        ValveDriver driver = valve.getMotorDriver();

        HX711 hx711 = valve.getLoadCell().getHX711();
        try {
            initialReadGrams = hx711.read();
            currentGrams = hx711.read();
            while ((isRunInfinity() || (currentGrams < initialReadGrams + goalGrams)) && !isCancelledExecutionThread()) {
                driver.setOpen(true);
                while ((isRunInfinity() || (currentGrams < initialReadGrams + goalGrams)) && !isCancelledExecutionThread()) {
                    currentGrams = hx711.read_once();
                }
                driver.setOpen(false);
                currentGrams = hx711.read(7);
            }
        } catch (InterruptedException e) {
            cancel();
        } catch (Exception e) {
            e.printStackTrace();
            cancel();
        }

    }

    @Override
    protected PumpJobState.RunningState genRunningState() {
        PumpJobState.RunningState runningState = new PumpJobState.RunningState();
        runningState.setForward(true);
        runningState.setRunInfinity(true);
        runningState.setPercentage((int) (100 * (currentGrams - initialReadGrams) / (goalGrams - initialReadGrams)));
        runningState.setJobId(getJobId());
        return runningState;
    }

    @Override
    protected JobMetrics genJobMetrics() {
        JobMetrics metrics = new JobMetrics();
        metrics.setId(getJobId());
        metrics.setMlPumped(currentGrams - initialReadGrams);
        metrics.setStartTime(getStartTime());
        metrics.setStopTime(getStopTime());
        return metrics;
    }
}
