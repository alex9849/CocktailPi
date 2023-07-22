package net.alex9849.cocktailmaker.model.pump.motortasks;

import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.model.pump.JobMetrics;
import net.alex9849.cocktailmaker.model.pump.PumpState;
import net.alex9849.motorlib.Direction;

import java.util.Optional;
import java.util.function.Consumer;

public class DcMotorTask extends PumpTask {
    DcPump dcPump;
    long duration;

    public DcMotorTask(Long prevJobId, DcPump dcPump, Direction direction, long duration, Consumer<Optional<PumpState.RunningState>> callback) {
        super(prevJobId, dcPump, duration == Long.MAX_VALUE, direction, callback);
        this.dcPump = dcPump;
        this.duration = duration;
    }

    @Override
    protected void pumpRun() {
        dcPump.getMotorDriver().setRunning(false);
    }

    @Override
    protected PumpState.RunningState genRunningState() {
        PumpState.RunningState runningState = new PumpState.RunningState();
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
