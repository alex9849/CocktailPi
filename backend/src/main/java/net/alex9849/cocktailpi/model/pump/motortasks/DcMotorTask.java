package net.alex9849.cocktailpi.model.pump.motortasks;

import net.alex9849.cocktailpi.model.pump.DcPump;
import net.alex9849.cocktailpi.model.pump.JobMetrics;
import net.alex9849.cocktailpi.model.pump.PumpJobState;
import net.alex9849.motorlib.motor.DCMotor;
import net.alex9849.motorlib.motor.Direction;

public class DcMotorTask extends PumpTask {
    DcPump dcPump;
    long duration;
    long remainingDuration;

    public DcMotorTask(Long prevJobId, DcPump dcPump, Direction direction, boolean isPumpUpDown, long duration) {
        super(prevJobId, dcPump, duration == Long.MAX_VALUE, isPumpUpDown, direction);
        this.dcPump = dcPump;
        this.duration = Math.max(1, duration);
        this.remainingDuration = this.duration;
    }

    @Override
    protected void runPump() {
        while (remainingDuration > 0 && !this.isCancelledExecutionThread()) {
            while (getState() == State.READY || getState() == State.SUSPENDING || getState() == State.SUSPENDED) {
                try {
                    wait();
                } catch (InterruptedException ignored) {}
                if(this.isCancelledExecutionThread()) {
                    return;
                }
            }
            DCMotor dcMotor = dcPump.getMotorDriver();
            dcMotor.setDirection(getDirection());
            dcMotor.setRunning(true);
            long startTime = System.currentTimeMillis();
            try {
                wait(remainingDuration);
            } catch (InterruptedException ignored) {
                //Ignore
            }
            dcMotor.setRunning(false);
            remainingDuration -= (System.currentTimeMillis() - startTime);
        }
    }

    @Override
    protected void doSuspend() {
        notify();
        dcPump.getMotorDriver().setRunning(false);
    }

    @Override
    protected PumpJobState.RunningState genRunningState() {
        PumpJobState.RunningState runningState = new PumpJobState.RunningState();
        runningState.setForward(getDirection() == Direction.FORWARD);
        runningState.setRunInfinity(isRunInfinity());
        runningState.setPercentage((int) (((getTimeElapsed()) * 100) / duration));
        runningState.setJobId(getJobId());
        return runningState;
    }

    public long getMlPumped() {
        return (getTimeElapsed() * 10) /  dcPump.getTimePerClInMs();
    }

    @Override
    protected JobMetrics genJobMetrics() {
        JobMetrics metrics = new JobMetrics();
        metrics.setId(getJobId());
        metrics.setMlPumped(getMlPumped());
        metrics.setStartTime(getStartTime());
        metrics.setStopTime(getStopTime());
        return metrics;
    }

}
