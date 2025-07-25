package net.alex9849.cocktailpi.service.pumps.cocktailfactory.productionstepworker;

import com.pi4j.exception.Pi4JException;
import net.alex9849.cocktailpi.model.pump.*;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.CocktailFactory;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.PumpPhase;
import net.alex9849.motorlib.motor.AcceleratingStepper;
import net.alex9849.motorlib.motor.MultiStepper;
import net.alex9849.motorlib.sensor.HX711;
import net.openhft.affinity.AffinityLock;

import java.util.*;
import java.util.concurrent.*;

public abstract class AbstractPumpingProductionStepWorker extends AbstractProductionStepWorker {
    private final ScheduledExecutorService scheduler;
    private Thread runner;
    private Set<PumpPhase> pumpPhases;
    private Map<StepperPump, Long> steppersToSteps;
    private Map<Valve, Long> valvesToRequestedGrams;
    private Map<Valve, Long> valvesToPumpedGrams;
    private Map<Pump, Integer> notUsedLiquid;
    private Set<Pump> usedPumps;
    private final Set<ScheduledFuture<?>> scheduledPumpFutures;
    private ScheduledFuture<?> notifierTask;

    private int requiredWorkTime;
    private long startTime;
    private long endTime;

    public AbstractPumpingProductionStepWorker(CocktailFactory cocktailFactory) {
        super(cocktailFactory);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.requiredWorkTime = 0;
        this.usedPumps = new HashSet<>();
        this.pumpPhases = new HashSet<>();
        this.steppersToSteps = new HashMap<>();
        this.valvesToRequestedGrams = new HashMap<>();
        this.valvesToPumpedGrams = new HashMap<>();
        this.scheduledPumpFutures = new HashSet<>();
        this.notUsedLiquid = new HashMap<>();
    }

    protected synchronized void setDcPumpPhases(Set<PumpPhase> pumpPhases) {
        Objects.requireNonNull(pumpPhases);
        if(this.isStarted()) {
            throw new IllegalStateException("Worker already started!");
        }
        this.pumpPhases = pumpPhases;
        this.requiredWorkTime = Math.max(this.requiredWorkTime,
                this.pumpPhases.stream().mapToInt(PumpPhase::getStopTime).max().orElse(0));
        for(PumpPhase pumpPhase : this.pumpPhases) {
            this.usedPumps.add(pumpPhase.getPump());
        }
    }

    protected synchronized void setValvesToRequestedGrams(Map<Valve, Long> valvesToRequestedGrams) {
        Objects.requireNonNull(valvesToRequestedGrams);
        if(this.isStarted()) {
            throw new IllegalStateException("Worker already started!");
        }
        this.valvesToRequestedGrams = valvesToRequestedGrams;
        this.requiredWorkTime = this.requiredWorkTime + valvesToRequestedGrams.entrySet()
                .stream().mapToInt((e) -> ((int) (e.getKey().getTimePerClInMs() * e.getValue()) / 10)).sum();
        this.usedPumps.addAll(valvesToRequestedGrams.keySet());
    }

    protected synchronized void setSteppersToComplete(Map<StepperPump, Long> steppersToSteps) {
        Objects.requireNonNull(steppersToSteps);
        if(this.isStarted()) {
            throw new IllegalStateException("Worker already started!");
        }
        for(Map.Entry<StepperPump, Long> entry : steppersToSteps.entrySet()) {
            AcceleratingStepper driver = entry.getKey().getMotorDriver();
            long cPos = driver.getCurrentPosition();
            long cTarget = driver.getTargetPosition();
            driver.setCurrentPosition(0);
            driver.moveTo(entry.getValue());
            this.requiredWorkTime = Math.max(this.requiredWorkTime, (int) driver.estimateTimeTillCompletion());
            driver.setCurrentPosition(cPos);
            driver.moveTo(cTarget);
        }
        this.steppersToSteps.putAll(steppersToSteps);
        this.usedPumps.addAll(steppersToSteps.keySet());
    }
    protected Set<PumpPhase> getDcPumpPhases() {
        return pumpPhases;
    }

    @Override
    public void start() {
        synchronized (this) {
            super.start();
            this.startTime = System.currentTimeMillis();
            this.endTime = this.startTime + this.getRequiredPumpingTime();
            CountDownLatch cl = new CountDownLatch(this.pumpPhases.size());

            for (PumpPhase pumpPhase : this.pumpPhases) {
                scheduledPumpFutures.add(scheduler.schedule(() -> {
                    try {
                        pumpPhase.getPump().getMotorDriver().setRunning(true);
                        pumpPhase.setStarted();
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Thread(() -> getCocktailFactory().cancelCocktail(true)).start();
                    }
                }, pumpPhase.getStartTime(), TimeUnit.MILLISECONDS));

                scheduledPumpFutures.add(scheduler.schedule(() -> {
                    try {
                        pumpPhase.getPump().getMotorDriver().setRunning(false);
                        pumpPhase.setStopped();
                        cl.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Thread(() -> getCocktailFactory().cancelCocktail(true)).start();
                    }
                }, pumpPhase.getStopTime(), TimeUnit.MILLISECONDS));
            }

            this.notifierTask = this.scheduler.scheduleAtFixedRate(this::notifySubscribers, 1, 1, TimeUnit.SECONDS);
            Runnable runTask = () -> {
                MultiStepper multiStepper = new MultiStepper();
                for(Map.Entry<StepperPump, Long> entry : steppersToSteps.entrySet()) {
                    AcceleratingStepper driver = entry.getKey().getMotorDriver();
                    driver.move(entry.getValue());
                    multiStepper.addStepper(driver);
                }
                try (AffinityLock al = AffinityLock.acquireCore()) {
                    while (multiStepper.runRound()) {
                        if(Thread.interrupted()) {
                            return;
                        }
                    }
                }
                try {
                    cl.await();
                } catch (InterruptedException e) {
                    return;
                }
                try {
                    Long initialReadGrams = null;
                    for(Map.Entry<Valve, Long> entry : valvesToRequestedGrams.entrySet()) {
                        Valve valve = entry.getKey();
                        ValveDriver driver = valve.getMotorDriver();
                        LoadCellReader lcReader = valve.getLoadCell().getLoadCellReader();

                        if(initialReadGrams == null) {
                            initialReadGrams = lcReader.readFromNow(7).get();
                        }
                        long currentGrams = initialReadGrams;
                        long goalGrams = entry.getValue();

                        long valveStartTime = System.currentTimeMillis();
                        long valveEndTime = System.currentTimeMillis();
                        try {
                            while (currentGrams < initialReadGrams + goalGrams) {
                                driver.setOpen(true);
                                while (currentGrams < initialReadGrams + goalGrams) {
                                    currentGrams = lcReader.readCurrent(1).get();
                                    if(Thread.interrupted()) {
                                        throw new InterruptedException();
                                    }
                                }
                                valveEndTime = System.currentTimeMillis();
                                driver.setOpen(false);
                                currentGrams = lcReader.readFromNow(7).get();
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            driver.setOpen(false);
                            try {
                                currentGrams = lcReader.readFromNow(7).get();
                            } catch (ExecutionException ignore) {}
                            valvesToPumpedGrams.put(valve, Math.max(0, currentGrams - initialReadGrams));
                            return;
                        }
                        initialReadGrams = currentGrams;
                        long valveTimeElapsed = valveEndTime - valveStartTime;
                        if(entry.getValue() > 0) {
                            valve.setTimePerClInMs((10 * valveTimeElapsed) / entry.getValue());
                        }
                        valvesToPumpedGrams.put(valve, goalGrams);
                    }
                } catch (InterruptedException|ExecutionException e) {
                    return;
                }

                onFinish();
            };
            runner = new Thread(runTask);
            runner.setPriority(Thread.MAX_PRIORITY);
            runner.setUncaughtExceptionHandler((t, e) -> {
                e.printStackTrace();
                getCocktailFactory().cancelCocktail(true);
            });
            runner.start();
        }
        this.notifySubscribers();
    }

    @Override
    public synchronized boolean cancel() {
        if(!super.cancel()) {
            return false;
        }
        for (ScheduledFuture<?> future : this.scheduledPumpFutures) {
            future.cancel(true);
        }
        if(this.runner != null) {
            try {
                this.runner.interrupt();
                this.runner.join();
            } catch (InterruptedException e) {
                //Ignore
            }
        }
        if(this.notifierTask != null) {
            this.notifierTask.cancel(false);
        }

        Map<Pump, Double> notUsedLiquidByPumpPrecise = new HashMap<>();
        for(PumpPhase pumpPhase : this.getDcPumpPhases()) {
            double notUsedLiquid = notUsedLiquidByPumpPrecise.computeIfAbsent(pumpPhase.getPump(), p -> 0d);
            notUsedLiquid += pumpPhase.getRemainingLiquidToPump();
            notUsedLiquidByPumpPrecise.put(pumpPhase.getPump(), notUsedLiquid);
        }
        if(this.isStarted()) {
            for(StepperPump stepperPump : this.steppersToSteps.keySet()) {
                double notUsedLiquid = (double) (stepperPump.getMotorDriver().distanceToGo() * 10) / stepperPump.getStepsPerCl();
                notUsedLiquidByPumpPrecise.put(stepperPump, notUsedLiquid);
            }
            for(Valve valve : this.valvesToRequestedGrams.keySet()) {
                long requestedLiquid = valvesToRequestedGrams.get(valve);
                Long pumpedLiquid = valvesToPumpedGrams.get(valve);
                if(pumpedLiquid != null) {
                    notUsedLiquidByPumpPrecise.put(valve, (double) Math.max(0, requestedLiquid - pumpedLiquid));
                }
            }
        } else {
            for(Map.Entry<StepperPump, Long> entry : this.steppersToSteps.entrySet()) {
                notUsedLiquidByPumpPrecise.put(entry.getKey(), 10 * entry.getValue().doubleValue() / entry.getKey().getStepsPerCl());
            }
            for(Valve valve : this.valvesToRequestedGrams.keySet()) {
                notUsedLiquidByPumpPrecise.put(valve, valvesToRequestedGrams.get(valve).doubleValue());
            }
        }
        notUsedLiquidByPumpPrecise.forEach((key, value) -> notUsedLiquid.put(key, (int) Math.round(value)));

        this.stopAllPumps();
        if (!this.scheduler.isShutdown()) {
            this.scheduler.shutdown();
        }

        return true;
    }

    @Override
    public StepProgress getProgress() {
        StepProgress progress = new StepProgress();
        if(this.isStarted()) {
            progress.setPercentCompleted(Math.min(100, (int) (((System.currentTimeMillis() - this.startTime) / ((double) (Math.max(1, this.endTime - this.startTime)))) * 100)));
        } else {
            progress.setPercentCompleted(0);
        }
        progress.setFinished(this.isFinished());
        return progress;
    }

    private synchronized void stopAllPumps() {
        for(Pump pump : this.usedPumps) {
            try {
                pump.shutdownDriver(false);
            } catch (Pi4JException e) {
                e.printStackTrace();
            }
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
        return this.notUsedLiquid;
    }

    public Set<Pump> getUsedPumps() {
        return usedPumps;
    }
}
