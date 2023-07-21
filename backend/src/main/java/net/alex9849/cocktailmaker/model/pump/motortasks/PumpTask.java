package net.alex9849.cocktailmaker.model.pump.motortasks;

import net.alex9849.cocktailmaker.model.pump.JobMetrics;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.model.pump.PumpState;
import net.alex9849.motorlib.Direction;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public abstract class PumpTask implements Runnable {
    private static long maxId;
    private final long id;
    private final Pump pump;
    private final CountDownLatch cdl;
    private final boolean runInfinity;
    private final Consumer<Optional<PumpState.RunningState>> callback;
    private final Direction direction;
    private PumpState.RunningState finishedRunningState;
    private JobMetrics finishedJobMetrics;
    private long startTime;
    private Long stopTime;
    private boolean cancelled;
    private Future<?> future;


    public PumpTask(Pump pump, boolean runInfinity, Direction direction, Consumer<Optional<PumpState.RunningState>> callback) {
        this.id = ++maxId;
        this.cdl = new CountDownLatch(1);
        this.pump = pump;
        this.direction = direction;
        this.runInfinity = runInfinity;
        this.startTime = System.currentTimeMillis();
        this.cancelled = false;
        this.callback = callback;
    }

    public void readify(Future<?> taskFuture) {
        this.future = taskFuture;
        this.startTime = System.currentTimeMillis();
        if(cancelled) {
            future.cancel(true);
        }
        cdl.countDown();
    }

    public long getId() {
        return id;
    }

    protected abstract void pumpRun();

    protected abstract PumpState.RunningState genRunningState();

    protected abstract JobMetrics genJobMetrics();

    protected long getTimeElapsed() {
        if(this.stopTime == null) {
            return System.currentTimeMillis() - this.startTime;
        }
        return this.stopTime - this.startTime;
    }

    @Override
    public void run() {
        try {
            cdl.await();
            pumpRun();
            doFinalize();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PumpState.RunningState getRunningState() {
        PumpState.RunningState runningState;
        if(finishedRunningState != null) {
            runningState = finishedRunningState;
        } else {
            runningState = genRunningState();
        }
        runningState.setJobId(id);
        return runningState;
    }

    public void cancel() {
        cancelled = true;
        if(future != null) {
            future.cancel(true);
        }
        doFinalize();
    }

    public JobMetrics getJobMetrics() {
        if(finishedJobMetrics != null) {
            return finishedJobMetrics;
        }
        return genJobMetrics();
    }

    public void doFinalize() {
        this.stopTime = System.currentTimeMillis();
        pump.getMotorDriver().shutdown();
        PumpState.RunningState runningState = getRunningState();
        this.finishedJobMetrics = getJobMetrics();
        this.finishedRunningState = runningState;
        callback.accept(Optional.of(runningState));
    }

    protected boolean isRunInfinity() {
        return runInfinity;
    }

    protected Direction getDirection() {
        return direction;
    }

    protected long getStartTime() {
        return startTime;
    }

    protected Long getStopTime() {
        return stopTime;
    }
}
