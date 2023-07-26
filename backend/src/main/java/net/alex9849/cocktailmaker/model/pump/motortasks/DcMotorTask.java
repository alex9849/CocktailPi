package net.alex9849.cocktailmaker.model.pump.motortasks;

import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.model.pump.JobMetrics;
import net.alex9849.cocktailmaker.model.pump.PumpJobState;
import net.alex9849.motorlib.DCMotor;
import net.alex9849.motorlib.Direction;

import java.util.Optional;
import java.util.function.Consumer;

public class DcMotorTask extends PumpTask {
    DcPump dcPump;
    long duration;

    public DcMotorTask(Long prevJobId, DcPump dcPump, Direction direction, boolean isPumpUpDown, long duration, Runnable callback) {
        super(prevJobId, dcPump, duration == Long.MAX_VALUE, isPumpUpDown, direction, callback);
        this.dcPump = dcPump;
        this.duration = Math.max(1, duration);
    }

    @Override
    protected void pumpRun() {
        DCMotor dcMotor = dcPump.getMotorDriver();
        dcMotor.setDirection(getDirection());
        dcMotor.setRunning(true);
        try {
            Thread.sleep(duration);
        } catch (InterruptedException ignored) {
            //Ignore
        }
        dcMotor.setRunning(false);
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
