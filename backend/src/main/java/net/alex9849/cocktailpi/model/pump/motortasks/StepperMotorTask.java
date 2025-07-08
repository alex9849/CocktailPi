package net.alex9849.cocktailpi.model.pump.motortasks;

import net.alex9849.cocktailpi.model.pump.JobMetrics;
import net.alex9849.cocktailpi.model.pump.PumpJobState;
import net.alex9849.cocktailpi.model.pump.StepperPump;
import net.alex9849.cocktailpi.service.pumps.StepperTaskWorker;
import net.alex9849.motorlib.motor.AcceleratingStepper;
import net.alex9849.motorlib.motor.Direction;
import net.openhft.affinity.AffinityLock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class StepperMotorTask extends PumpTask {
    StepperPump stepperPump;
    long stepsToRun;
    private Future<Void> taskFuture;
    private long remainingSteps;

    /**
     * @param stepsToRun Long.MAX_VALUE == unlimited
     */
    public StepperMotorTask(Long prevJobId, StepperPump stepperPump, Direction direction, boolean isPumpUpDown, long stepsToRun) {
        super(prevJobId, stepperPump, stepsToRun == Long.MAX_VALUE, isPumpUpDown, direction);
        this.stepperPump = stepperPump;
        if(isRunInfinity()) {
            stepsToRun = 10000000000L;
        }
        this.stepsToRun = Math.max(1, stepsToRun);
        if (getDirection() == Direction.BACKWARD) {
            this.remainingSteps = -this.stepsToRun;
        } else {
            this.remainingSteps = this.stepsToRun;
        }
    }

    public long getMlPumped() {
        return (getStepsMade() * 10) / stepperPump.getStepsPerCl();
    }

    public long getStepsMade() {
        return stepsToRun - Math.abs(stepperPump.getMotorDriver().distanceToGo());
    }

    @Override
    public void cancel() {
        super.cancel();
        StepperTaskWorker.getInstance().cancelTask(stepperPump.getMotorDriver());
    }

    @Override
    protected void runPump() {
        while (this.remainingSteps > 0 && !this.isCancelledExecutionThread()) {
            if(this.isCancelledExecutionThread()) {
               return;
            }
            while (getState() == State.READY || getState() == State.SUSPENDING || getState() == State.SUSPENDED) {
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException ignored) {}
                if(this.isCancelledExecutionThread()) {
                    return;
                }
            }
            AcceleratingStepper driver = stepperPump.getMotorDriver();
            driver.move(this.remainingSteps);
            this.remainingSteps = 0;
            taskFuture = StepperTaskWorker.getInstance().submitTask(driver);
            while (driver.distanceToGo() != 0 && !isCancelledExecutionThread() && getState() != State.SUSPENDED) {
                try {
                    taskFuture.get();
                } catch (ExecutionException | InterruptedException ignored) {}
            }
        }
    }

    @Override
    protected void doSuspend() {
        AcceleratingStepper driver = stepperPump.getMotorDriver();
        long remainingSteps = driver.distanceToGo();
        long stepsToStopNow = (long) Math.ceil(Math.pow(driver.getSpeed(), 2.0F) / ((double) 2.0F * driver.getAcceleration()));
        remainingSteps = remainingSteps - stepsToStopNow;
        this.remainingSteps = remainingSteps;
        driver.move(stepsToStopNow);
        while (driver.distanceToGo() != 0 && !isCancelledExecutionThread()) {
            try {
                taskFuture.get();
            } catch (ExecutionException | InterruptedException ignored) {}
        }
        driver.shutdown();
        driver.move(remainingSteps);
    }

    @Override
    protected PumpJobState.RunningState genRunningState() {
        PumpJobState.RunningState runningState = new PumpJobState.RunningState();
        runningState.setPercentage((int) (getStepsMade() * 100 / stepsToRun));
        runningState.setForward(getDirection() == Direction.FORWARD);
        runningState.setState(getState());
        runningState.setRunInfinity(isRunInfinity());
        runningState.setJobId(getJobId());
        return runningState;
    }

    @Override
    protected JobMetrics genJobMetrics() {
        JobMetrics metrics = new JobMetrics();
        metrics.setId(getJobId());
        metrics.setMlPumped(getMlPumped());
        metrics.setStartTime(getStartTime());
        metrics.setStopTime(getStopTime());
        metrics.setStepsMade(getStepsMade());
        metrics.setException(null);
        return metrics;
    }
}
