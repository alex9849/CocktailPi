package net.alex9849.cocktailpi.model.pump.motortasks;

import lombok.Getter;
import net.alex9849.cocktailpi.model.pump.JobMetrics;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.pump.PumpJobState;
import net.alex9849.motorlib.motor.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

public abstract class PumpTask implements Runnable {
    public enum State {
        READY, RUNNING, ERROR, FINISHED, SUSPENDING, SUSPENDED
    }
    private static long maxId;
    @Getter
    private final long jobId;
    @Getter
    private final Long prevJobId;
    @Getter
    private final Pump pump;
    private final CountDownLatch cdl;
    private final boolean runInfinity;

    private final boolean isPumpUpDown;
    private final List<Runnable> callbacks;
    private final Direction direction;
    private PumpJobState.RunningState finishedRunningState;
    private JobMetrics finishedJobMetrics;
    private long startTime;
    private Long stopTime;
    private long timeElapsed;
    private Long timeElapsedStartTrack;
    private boolean cancelled;
    @Getter
    protected State state;
    private Future<?> future;
    protected final Object signalLock;
    protected final Object stateLock;


    public PumpTask(Long prevJobId, Pump pump, boolean runInfinity, boolean isPumpUpDown, Direction direction) {
        this.prevJobId = prevJobId;
        this.jobId = ++maxId;
        this.cdl = new CountDownLatch(1);
        this.pump = pump;
        this.isPumpUpDown = isPumpUpDown;
        this.direction = direction;
        this.runInfinity = runInfinity;
        this.startTime = System.currentTimeMillis();
        this.cancelled = false;
        this.callbacks = new ArrayList<>();
        this.signalLock = new Object();
        this.stateLock = new Object();
        this.state = State.READY;
    }

    public void readify(Future<?> taskFuture) {
        this.future = taskFuture;
        this.startTime = System.currentTimeMillis();
        if(cancelled) {
            future.cancel(true);
        }
        cdl.countDown();
    }

    protected abstract void runPump();

    protected abstract PumpJobState.RunningState genRunningState();

    protected abstract JobMetrics genJobMetrics();

    protected boolean isPumpUpDown() {
        return isPumpUpDown;
    }

    protected long getTimeElapsed() {
        if(timeElapsedStartTrack == null) {
            return timeElapsed;
        }
        return timeElapsed + (System.currentTimeMillis() - timeElapsedStartTrack);
    }

    protected void startRunTimeTrack() {
        if (timeElapsedStartTrack == null) {
            timeElapsedStartTrack = System.currentTimeMillis();
        }
    }

    protected void stopRunTimeTrack() {
        if (timeElapsedStartTrack == null) {
            return;
        }
        timeElapsed += System.currentTimeMillis() - timeElapsedStartTrack;
        timeElapsedStartTrack = null;
    }

    protected void triggerCallbacks() {
        for (Runnable callback : callbacks) {
            callback.run();
        }
    }

    public void addCompletionCallBack(Runnable runnable) {
        this.callbacks.add(runnable);
    }

    public boolean signalStart() {
        synchronized (stateLock) {
            if (getState() == State.RUNNING) {
                synchronized (signalLock) {
                    signalLock.notify();
                }
                return true;
            }
            if(getState() == State.READY || getState() == State.SUSPENDING || getState() == State.SUSPENDED) {
                this.state = State.RUNNING;
                startRunTimeTrack();
                synchronized (signalLock) {
                    signalLock.notify();
                }
                return true;
            }
            return false;
        }
    }

    public boolean suspend() {
        synchronized (stateLock) {
            if(this.state != State.RUNNING) {
                return false;
            }
            this.state = State.SUSPENDING;
        }
        doSuspend();
        synchronized (stateLock) {
            if(getState() == State.SUSPENDING) {
                this.state = State.SUSPENDED;
            }
        }
        stopRunTimeTrack();
        return true;
    }

    protected abstract void doSuspend();

    @Override
    public void run() {
        try {
            cdl.await();
            this.startTime = System.currentTimeMillis();
            runPump();

            if(isPumpUpDown && !isCancelledExecutionThread()) {
                pump.setPumpedUp(getDirection() == Direction.FORWARD);
            }

            this.stopTime = System.currentTimeMillis();
            stopRunTimeTrack();
            PumpJobState.RunningState runningState = getRunningState();
            this.finishedJobMetrics = getJobMetrics();
            this.finishedRunningState = runningState;
            pump.shutdownDriver(false);
            synchronized (stateLock) {
                this.state = State.FINISHED;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            this.stopTime = System.currentTimeMillis();
            stopRunTimeTrack();
            this.finishedJobMetrics = genJobMetrics();
            this.finishedRunningState = genRunningState();
            this.finishedJobMetrics.setException(e);
            synchronized (stateLock) {
                this.state = State.ERROR;
            }
        } finally {
            triggerCallbacks();
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
        boolean interrupted = Thread.interrupted();
        if(cancelled) {
            return true;
        }
        if(isFinished()) {
            return false;
        }
        if(interrupted) {
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
