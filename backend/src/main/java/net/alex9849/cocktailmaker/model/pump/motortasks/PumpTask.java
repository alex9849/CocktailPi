package net.alex9849.cocktailmaker.model.pump.motortasks;

import net.alex9849.cocktailmaker.model.pump.JobMetrics;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.model.pump.PumpJobState;
import net.alex9849.motorlib.motor.Direction;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

public abstract class PumpTask implements Runnable {
    private static long maxId;
    private final long jobId;
    private final Long prevJobId;
    private final Pump pump;
    private final CountDownLatch cdl;
    private final boolean runInfinity;

    private final boolean isPumpUpDown;
    private final Runnable callback;
    private final Direction direction;
    private PumpJobState.RunningState finishedRunningState;
    private JobMetrics finishedJobMetrics;
    private long startTime;
    private Long stopTime;
    private boolean cancelled;
    private Future<?> future;


    public PumpTask(Long prevJobId, Pump pump, boolean runInfinity, boolean isPumpUpDown, Direction direction, Runnable callback) {
        this.prevJobId = prevJobId;
        this.jobId = ++maxId;
        this.cdl = new CountDownLatch(1);
        this.pump = pump;
        this.isPumpUpDown = isPumpUpDown;
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

    public long getJobId() {
        return jobId;
    }

    protected abstract void pumpRun();

    protected abstract PumpJobState.RunningState genRunningState();

    protected abstract JobMetrics genJobMetrics();

    protected long getTimeElapsed() {
        if(this.stopTime == null) {
            return System.currentTimeMillis() - this.startTime;
        }
        return this.stopTime - this.startTime;
    }

    public Long getPrevJobId() {
        return prevJobId;
    }

    @Override
    public void run() {
        try {
            cdl.await();
            this.startTime = System.currentTimeMillis();
            pumpRun();

            if(isPumpUpDown && !isCancelledExecutionThread()) {
                pump.setPumpedUp(getDirection() == Direction.FORWARD);
            }

            pump.getMotorDriver().shutdown();
            this.stopTime = System.currentTimeMillis();
            PumpJobState.RunningState runningState = getRunningState();
            this.finishedJobMetrics = getJobMetrics();
            this.finishedRunningState = runningState;
            callback.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PumpJobState.RunningState getRunningState() {
        PumpJobState.RunningState runningState;
        if(finishedRunningState != null) {
            runningState = finishedRunningState;
        } else {
            runningState = genRunningState();
        }
        runningState.setJobId(jobId);
        return runningState;
    }

    public void cancel() {
        cancelled = true;
        if(future != null) {
            future.cancel(true);
        }
    }

    protected boolean isCancelledExecutionThread() {
        if(cancelled) {
            return true;
        }
        if(isFinished()) {
            return false;
        }
        if(Thread.interrupted()) {
            cancelled = true;
        }
        return cancelled;
    }

    public JobMetrics getJobMetrics() {
        if(finishedJobMetrics != null) {
            return finishedJobMetrics;
        }
        return genJobMetrics();
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

    public boolean isFinished() {
        return stopTime != null;
    }
}
