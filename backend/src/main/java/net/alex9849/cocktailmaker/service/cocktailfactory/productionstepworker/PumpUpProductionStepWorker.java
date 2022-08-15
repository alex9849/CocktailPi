package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.service.cocktailfactory.PumpPhase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PumpUpProductionStepWorker extends AbstractProductionStepWorker {
    private final List<Pump> requiredPumps;
    private final Set<ScheduledFuture<?>> scheduledTasks;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> finishTask;
    private final List<PumpPhase> pumpPhases;
    private final long requiredWorkTime;
    private long startTime;
    private long endTime;

    public PumpUpProductionStepWorker(List<Pump> requiredPumps) {
        this.requiredPumps = requiredPumps;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduledTasks = new HashSet<>();
        this.pumpPhases = new ArrayList<>();
        long longestRuntime = 0;
        for(Pump pump : this.requiredPumps) {
            if(pump.isPumpedUp()) {
                continue;
            }
            int runtime = pump.getConvertMlToRuntime(pump.getTubeCapacityInMl());
            PumpPhase pumpPhase = new PumpPhase(0, runtime, pump);
            this.pumpPhases.add(pumpPhase);
            if(longestRuntime < runtime) {
                longestRuntime = runtime;
            }
        }
        this.requiredWorkTime = longestRuntime;

    }


    @Override
    public void start() {
        super.start();
        this.startTime = System.currentTimeMillis();
        long longestRuntime = 0;
        for(PumpPhase pumpPhase : this.pumpPhases) {
            pumpPhase.getPump().setRunning(true);
            scheduledTasks.add(scheduler.schedule(() -> {
                pumpPhase.getPump().setRunning(false);
                pumpPhase.getPump().setPumpedUp(true);
            }, pumpPhase.getStopTime(), TimeUnit.MILLISECONDS));
        }
        this.finishTask = scheduler.schedule(this::setFinished, longestRuntime, TimeUnit.MILLISECONDS);
        this.endTime = this.startTime + this.requiredWorkTime;
    }

    @Override
    public void cancel() {
        for (ScheduledFuture<?> future : this.scheduledTasks) {
            future.cancel(true);
        }
        if(this.finishTask != null) {
            finishTask.cancel(true);
        }
        requiredPumps.forEach(p -> p.setRunning(false));
        if (!this.scheduler.isShutdown()) {
            this.scheduler.shutdown();
        }
    }

    @Override
    public StepProgress getProgress() {
        StepProgress progress = new StepProgress();
        if(this.isStarted()) {
            progress.setPercentCompleted(Math.min(100, (int) (((System.currentTimeMillis() - this.startTime) / ((double) (this.endTime - this.startTime))) * 100)));
        } else {
            progress.setPercentCompleted(0);
        }
        progress.setFinished(this.isFinished());
        return progress;
    }

    public long getRequiredPumpingTime() {
        return this.requiredWorkTime;
    }
}
