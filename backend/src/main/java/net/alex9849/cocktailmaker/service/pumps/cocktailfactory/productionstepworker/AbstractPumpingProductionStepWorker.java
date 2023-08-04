package net.alex9849.cocktailmaker.service.pumps.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.model.pump.StepperPump;
import net.alex9849.cocktailmaker.service.pumps.cocktailfactory.PumpPhase;
import net.alex9849.motorlib.AcceleratingStepper;
import net.alex9849.motorlib.MultiStepper;
import net.alex9849.motorlib.StepperDriver;

import java.util.*;
import java.util.concurrent.*;

public abstract class AbstractPumpingProductionStepWorker extends AbstractProductionStepWorker {
    private final ScheduledExecutorService scheduler;
    private Thread runner;
    private Set<PumpPhase> pumpPhases;
    private Set<AcceleratingStepper> stepperDriverToComplete;
    private Set<Pump> usedPumps;
    private final Set<ScheduledFuture<?>> scheduledPumpFutures;
    private ScheduledFuture<?> notifierTask;

    private int requiredWorkTime;
    private long startTime;
    private long endTime;

    public AbstractPumpingProductionStepWorker() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.requiredWorkTime = 0;
        this.usedPumps = new HashSet<>();
        this.pumpPhases = new HashSet<>();
        this.stepperDriverToComplete = new HashSet<>();
        this.scheduledPumpFutures = new HashSet<>();
    }

    protected synchronized void setDcPumpPhases(Set<PumpPhase> pumpPhases) {
        Objects.requireNonNull(pumpPhases);
        if(this.isStarted()) {
            throw new IllegalStateException("Worker already started!");
        }
        this.pumpPhases = pumpPhases;
        this.requiredWorkTime = Math.max(this.requiredWorkTime,
                this.pumpPhases.stream().mapToInt(PumpPhase::getStopTime).max().orElse(0));
        this.usedPumps = new HashSet<>();
        for(PumpPhase pumpPhase : this.pumpPhases) {
            this.usedPumps.add(pumpPhase.getPump());
        }
    }

    protected synchronized void setDriversToComplete(Set<AcceleratingStepper> drivers) {
        Objects.requireNonNull(drivers);
        if(this.isStarted()) {
            throw new IllegalStateException("Worker already started!");
        }
        for(AcceleratingStepper driver : drivers) {
            this.requiredWorkTime = Math.max(this.requiredWorkTime, (int) driver.estimateTimeTillCompletion());
            stepperDriverToComplete.add(driver);
        }
    }

    protected void setUsedPumps(Set<Pump> usedPumps) {
        this.usedPumps = usedPumps;
    }

    protected Set<PumpPhase> getDcPumpPhases() {
        return pumpPhases;
    }

    @Override
    public synchronized void start() {
        super.start();
        this.startTime = System.currentTimeMillis();
        this.endTime = this.startTime + this.getRequiredPumpingTime();
        CountDownLatch cl = new CountDownLatch(this.pumpPhases.size());

        for (PumpPhase pumpPhase : this.pumpPhases) {
            scheduledPumpFutures.add(scheduler.schedule(() -> {
                pumpPhase.getPump().getMotorDriver().setRunning(true);
                pumpPhase.setStarted();
            }, pumpPhase.getStartTime(), TimeUnit.MILLISECONDS));

            scheduledPumpFutures.add(scheduler.schedule(() -> {
                pumpPhase.getPump().getMotorDriver().setRunning(false);
                pumpPhase.setStopped();
                cl.countDown();
            }, pumpPhase.getStopTime(), TimeUnit.MILLISECONDS));
        }

        Runnable runTask = () -> {
            MultiStepper multiStepper = new MultiStepper();
            stepperDriverToComplete.forEach(multiStepper::addStepper);
            multiStepper.prepareRun();
            while (multiStepper.runRound()) {
                Thread.yield();
                if(Thread.interrupted()) {
                    return;
                }
            }
            try {
                cl.await();
            } catch (InterruptedException e) {
                return;
            }
            onFinish();
        };
        runner = new Thread(runTask);
        runner.start();

        this.notifierTask = this.scheduler.scheduleAtFixedRate(this::notifySubscribers, 1, 1, TimeUnit.SECONDS);
        this.notifySubscribers();
    }

    @Override
    public synchronized void cancel() {
        for (ScheduledFuture<?> future : this.scheduledPumpFutures) {
            future.cancel(true);
        }
        if(this.runner != null) {
            this.runner.interrupt();
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
            if(pumpPhase.getPump().getMotorDriver().isRunning()) {
                pumpPhase.getPump().getMotorDriver().setRunning(false);
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

    public Map<Pump, Integer> getNotUsedLiquid() {
        Map<Pump, Double> notUsedLiquidByPumpPrecise = new HashMap<>();
        for(PumpPhase pumpPhase : this.getDcPumpPhases()) {
            double notUsedLiquid = notUsedLiquidByPumpPrecise.computeIfAbsent(pumpPhase.getPump(), p -> 0d);
            notUsedLiquid += pumpPhase.getRemainingLiquidToPump();
            notUsedLiquidByPumpPrecise.put(pumpPhase.getPump(), notUsedLiquid);
        }
        Map<Pump, Integer> notUsedLiquidByPump = new HashMap<>();
        for(Map.Entry<Pump, Double> entry : notUsedLiquidByPumpPrecise.entrySet()) {
            notUsedLiquidByPump.put(entry.getKey(), (int) Math.round(entry.getValue().doubleValue()));
        }
        return notUsedLiquidByPump;
    }

    public Set<Pump> getUsedPumps() {
        return usedPumps;
    }
}
