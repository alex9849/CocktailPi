package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.service.cocktailfactory.PumpPhase;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractPumpingProductionStepWorker extends AbstractProductionStepWorker {
    private final ScheduledExecutorService scheduler;
    private Set<PumpPhase> pumpPhases;
    private Set<Pump> usedPumps;
    private final Set<ScheduledFuture<?>> scheduledPumpFutures;
    private ScheduledFuture<?> finishTask;
    private ScheduledFuture<?> notifierTask;

    private int requiredWorkTime;
    private long startTime;
    private long endTime;

    public AbstractPumpingProductionStepWorker() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.requiredWorkTime = 0;
        this.usedPumps = new HashSet<>();
        this.pumpPhases = new HashSet<>();
        this.scheduledPumpFutures = new HashSet<>();
    }

    protected void setPumpPhases(Set<PumpPhase> pumpPhases) {
        Objects.requireNonNull(pumpPhases);
        if(this.isStarted()) {
            throw new IllegalStateException("Worker already started!");
        }
        this.pumpPhases = pumpPhases;
        this.requiredWorkTime = this.pumpPhases.stream().mapToInt(PumpPhase::getStopTime).max().orElse(0);
        this.usedPumps = new HashSet<>();
        for(PumpPhase pumpPhase : this.pumpPhases) {
            this.usedPumps.add(pumpPhase.getPump());
        }
    }

    protected Set<PumpPhase> getPumpPhases() {
        return pumpPhases;
    }

    @Override
    public void start() {
        super.start();
        this.startTime = System.currentTimeMillis();
        this.endTime = this.startTime + this.getRequiredPumpingTime();

        for (PumpPhase pumpPhase : this.pumpPhases) {
            scheduledPumpFutures.add(scheduler.schedule(() -> {
                pumpPhase.getPump().setRunning(true);
                pumpPhase.setStarted();
            }, pumpPhase.getStartTime(), TimeUnit.MILLISECONDS));

            scheduledPumpFutures.add(scheduler.schedule(() -> {
                pumpPhase.getPump().setRunning(false);
                pumpPhase.setStopped();
            }, pumpPhase.getStopTime(), TimeUnit.MILLISECONDS));
        }

        this.notifierTask = this.scheduler.scheduleAtFixedRate(this::notifySubscribers, 1, 1, TimeUnit.SECONDS);
        this.finishTask = scheduler.schedule(this::onFinish, this.getRequiredPumpingTime(), TimeUnit.MILLISECONDS);
        this.notifySubscribers();
    }

    @Override
    public void cancel() {
        for (ScheduledFuture<?> future : this.scheduledPumpFutures) {
            future.cancel(true);
        }
        if(this.finishTask != null) {
            this.finishTask.cancel(true);
        }
        if(this.notifierTask != null) {
            this.notifierTask.cancel(false);
        }
        this.stopAllPumps();
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

    private void stopAllPumps() {
        Set<Long> seenPumps = new HashSet<>();
        for(PumpPhase pumpPhase : this.pumpPhases) {
            if(seenPumps.contains(pumpPhase.getPump().getId())) {
                continue;
            }
            pumpPhase.getPump().setRunning(false);
            if(pumpPhase.getState() == PumpPhase.State.RUNNING) {
                pumpPhase.setStopped();
            }
            seenPumps.add(pumpPhase.getPump().getId());
        }
    }

    protected void onFinish() {
        this.scheduledPumpFutures.forEach(x -> x.cancel(true));
        this.notifierTask.cancel(false);
        this.stopAllPumps();
        this.setFinished();
    }

    public long getRequiredPumpingTime() {
        return this.requiredWorkTime;
    }

    public Map<Pump, Integer> getNotUsedLiquid() {
        Map<Pump, Integer> notUsedLiquidByPump = new HashMap<>();
        for(PumpPhase pumpPhase : this.getPumpPhases()) {
            int notUsedLiquid = notUsedLiquidByPump.computeIfAbsent(pumpPhase.getPump(), p -> 0);
            notUsedLiquid += pumpPhase.getRemainingLiquidToPump();
            notUsedLiquidByPump.put(pumpPhase.getPump(), notUsedLiquid);
        }
        return notUsedLiquidByPump;
    }

    public Set<Pump> getUsedPumps() {
        return usedPumps;
    }
}
