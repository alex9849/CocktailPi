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
    private AcceleratingStepper driver;


    /**
     * @param stepsToRun Long.MAX_VALUE == unlimited
     */
    public StepperMotorTask(Long prevJobId, StepperPump stepperPump, Direction direction, boolean isPumpUpDown, long stepsToRun, Runnable callback) {
        super(prevJobId, stepperPump, stepsToRun == Long.MAX_VALUE, isPumpUpDown, direction, callback);
        this.stepperPump = stepperPump;
        this.stepsToRun = Math.max(1, stepsToRun);
    }

    public long getMlPumped() {
        return (getStepsMade() * 10) / stepperPump.getStepsPerCl();
    }

    public long getStepsMade() {
        return stepsToRun - Math.abs(driver.distanceToGo());
    }

    @Override
    public void cancel() {
        super.cancel();
        if(this.driver != null) {
            StepperTaskWorker.getInstance().cancelTask(this.driver);
        }
    }

    @Override
    protected void pumpRun() {
        this.driver = stepperPump.getMotorDriver();
        if (isRunInfinity()) {
            //Pick a very large number
            stepsToRun = 10000000000L;
        } else if (stepsToRun == 0) {
            stepsToRun = 1;
        }
        if (getDirection() == Direction.BACKWARD) {
            driver.move(-stepsToRun);
        } else {
            driver.move(stepsToRun);
        }
        Future<Void> future = StepperTaskWorker.getInstance().submitTask(driver);
        while (driver.distanceToGo() != 0 && !isCancelledExecutionThread()) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException ignored) {}
        }
    }

    @Override
    protected PumpJobState.RunningState genRunningState() {
        PumpJobState.RunningState runningState = new PumpJobState.RunningState();
        runningState.setPercentage((int) (getStepsMade() * 100 / stepsToRun));
        runningState.setForward(getDirection() == Direction.FORWARD);
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
        return metrics;
    }
}
