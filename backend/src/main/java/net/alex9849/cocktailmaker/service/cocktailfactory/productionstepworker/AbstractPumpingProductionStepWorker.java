package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.service.cocktailfactory.PumpPhase;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractPumpingProductionStepWorker extends AbstractProductionStepWorker {
    private final ScheduledExecutorService scheduler;
    private Set<PumpPhase> pumpPhases;
    private Set<DcPump> usedPumps;
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

    protected synchronized void setPumpPhases(Set<PumpPhase> pumpPhases) {
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
    public synchronized void start() {
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
    public synchronized void cancel() {
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

    private synchronized void stopAllPumps() {
        Set<Long> seenPumps = new HashSet<>();
        for(PumpPhase pumpPhase : this.pumpPhases) {
            if(seenPumps.contains(pumpPhase.getPump().getId())) {
                continue;
            }
            if(pumpPhase.getPump().isRunning()) {
                pumpPhase.getPump().setRunning(false);
            }
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

    public Map<DcPump, Integer> getNotUsedLiquid() {
        Map<DcPump, Double> notUsedLiquidByPumpPrecise = new HashMap<>();
        for(PumpPhase pumpPhase : this.getPumpPhases()) {
            double notUsedLiquid = notUsedLiquidByPumpPrecise.computeIfAbsent(pumpPhase.getPump(), p -> 0d);
            notUsedLiquid += pumpPhase.getRemainingLiquidToPump();
            notUsedLiquidByPumpPrecise.put(pumpPhase.getPump(), notUsedLiquid);
        }
        Map<DcPump, Integer> notUsedLiquidByPump = new HashMap<>();
        for(Map.Entry<DcPump, Double> entry : notUsedLiquidByPumpPrecise.entrySet()) {
            notUsedLiquidByPump.put(entry.getKey(), (int) Math.round(entry.getValue().doubleValue()));
        }
        return notUsedLiquidByPump;
    }

    public Set<DcPump> getUsedPumps() {
        return usedPumps;
    }
}
