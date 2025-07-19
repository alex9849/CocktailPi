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
        DCMotor dcMotor = dcPump.getMotorDriver();
        dcMotor.setDirection(getDirection());
        while (remainingDuration > 0 && !this.isCancelledExecutionThread()) {
            while (getState() == State.READY || getState() == State.SUSPENDING || getState() == State.SUSPENDED) {
                try {
                    synchronized (signalLock) {
                        signalLock.wait();
                    }
                } catch (InterruptedException ignored) {}
                if(this.isCancelledExecutionThread()) {
                    return;
                }
            }
            dcMotor.setRunning(true);
            long startTime = System.currentTimeMillis();
            try {
                synchronized (signalLock) {
                    if (remainingDuration > 0) {
                        signalLock.wait(remainingDuration);
                    }
                }
            } catch (InterruptedException ignored) {}
            if(this.isCancelledExecutionThread()) {
                dcMotor.setRunning(false);
                return;
            }
            remainingDuration -= (System.currentTimeMillis() - startTime);
        }
        dcMotor.setRunning(false);
    }

    @Override
    protected void doSuspend() {
        synchronized (signalLock) {
            signalLock.notify();
        }
        dcPump.getMotorDriver().setRunning(false);
    }

    @Override
    protected PumpJobState.RunningState genRunningState() {
        PumpJobState.RunningState runningState = new PumpJobState.RunningState();
        runningState.setForward(getDirection() == Direction.FORWARD);
        runningState.setRunInfinity(isRunInfinity());
        runningState.setState(getState());
        if(duration == 0) {
            runningState.setPercentage(0);
        } else {
            runningState.setPercentage((int) (((getTimeElapsed()) * 100) / duration));
        }
        runningState.setJobId(getJobId());
        return runningState;
    }

    public long getMlPumped() {
        Integer timePerCl = dcPump.getTimePerClInMs();
        if(timePerCl == null || timePerCl == 0) {
            return 0;
        } else {
            return (getTimeElapsed() * 10) /  timePerCl;
        }
    }

    @Override
    protected JobMetrics genJobMetrics() {
        JobMetrics metrics = new JobMetrics();
        metrics.setId(getJobId());
        metrics.setMlPumped(getMlPumped());
        metrics.setStartTime(getStartTime());
        metrics.setStopTime(getStopTime());
        metrics.setTimeElapsed(getTimeElapsed());
        metrics.setException(null);
        return metrics;
    }

}
