package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.iface.IGpioController;
import net.alex9849.cocktailmaker.iface.IGpioPin;
import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.ProductionStepIngredient;
import net.alex9849.cocktailmaker.service.cocktailfactory.PumpPhase;
import net.alex9849.cocktailmaker.service.cocktailfactory.PumpTimingStepCalculator;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AutomaticProductionStepWorker extends AbstractProductionStepWorker {
    private final Map<Long, List<Pump>> pumpsByIngredientId;
    private final List<ProductionStepIngredient> productionStepInstructions;
    private final Map<Pump, List<PumpPhase>> pumpPumpPhases;
    private final Set<Pump> usedPumps;
    private final long requiredWorkingTime;
    private final ScheduledExecutorService scheduler;
    private IGpioController gpioController;
    private Set<ScheduledFuture> scheduledPumpFutures;
    private ScheduledFuture finishTask;
    private ScheduledFuture notifierTask;

    private long startTime;
    private long endTime;
    private boolean started;

    /**
     *
     * @param pumps pumps is an output parameter! The attribute fillingLevelInMl will be decreased according to the recipe
     */
    public AutomaticProductionStepWorker(Set<Pump> pumps, IGpioController gpioController, List<ProductionStepIngredient> productionStepInstructions, int minimalPumpTime, int minimalBreakTime) {
        this.pumpsByIngredientId = pumps.stream()
                .filter(x -> x.getCurrentIngredient() != null)
                .collect(Collectors
                .groupingBy(x -> x.getCurrentIngredient().getId()));
        this.productionStepInstructions = productionStepInstructions;

        Map<ProductionStepIngredient, List<Pump>> matchingPumpByProductionStepIngredient = new HashMap<>();
        for (ProductionStepIngredient instruction : this.productionStepInstructions) {
            if (!(instruction.getIngredient() instanceof AutomatedIngredient)) {
                throw new IllegalArgumentException("Can't automatically fulfill productionstep. One or more given ingredients can only be added manually!");
            }
            AutomatedIngredient currAutoIngredient = (AutomatedIngredient) instruction.getIngredient();
            List<Pump> pumpsWithIngredient = this.pumpsByIngredientId.get(currAutoIngredient.getId());
            if (pumpsWithIngredient == null) {
                throw new IllegalArgumentException("Can't automatically fulfill productionstep. One or more required ingredients are not assigned to pumps!");
            }
            matchingPumpByProductionStepIngredient.put(instruction, pumpsWithIngredient);
        }
        PumpTimingStepCalculator pumpTimingStepCalculator = new PumpTimingStepCalculator(matchingPumpByProductionStepIngredient,
                minimalPumpTime, minimalBreakTime);
        this.usedPumps = pumpTimingStepCalculator.getUsedPumps();
        this.pumpPumpPhases = pumpTimingStepCalculator.getPumpPhases();
        this.requiredWorkingTime = pumpTimingStepCalculator.getLongestIngredientTime();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.gpioController = gpioController;
        this.scheduledPumpFutures = new HashSet<>();
        this.started = false;
    }

    public void start() {
        if (started) {
            throw new IllegalStateException("ProductionStepWorker has already been started!");
        }
        this.started = true;
        this.startTime = System.currentTimeMillis();
        for (Map.Entry<Pump, List<PumpPhase>> currPumpWithTimings : this.pumpPumpPhases.entrySet()) {
            for (PumpPhase pumpPhase : currPumpWithTimings.getValue()) {
                scheduledPumpFutures.add(scheduler.schedule(() -> {
                    gpioController.getGpioPin(pumpPhase.getPump().getGpioPin()).setLow();
                }, pumpPhase.getStartTime(), TimeUnit.MILLISECONDS));
                scheduledPumpFutures.add(scheduler.schedule(() -> {
                    gpioController.getGpioPin(pumpPhase.getPump().getGpioPin()).setHigh();
                }, pumpPhase.getStopTime(), TimeUnit.MILLISECONDS));
            }
        }
        long delayLastInstruction = this.scheduledPumpFutures.stream()
                .mapToLong(x -> x.getDelay(TimeUnit.MILLISECONDS)).max().getAsLong();

        this.finishTask = scheduler.schedule(this::onFinish, delayLastInstruction, TimeUnit.MILLISECONDS);
        this.notifierTask = this.scheduler.scheduleAtFixedRate(this::notifySubscribers, 1, 1, TimeUnit.SECONDS);
        this.endTime = System.currentTimeMillis() + delayLastInstruction;
        this.notifySubscribers();
    }

    public Set<Pump> getUsedPumps() {
        return usedPumps;
    }

    private void onFinish() {
        this.scheduledPumpFutures.forEach(x -> x.cancel(true));
        this.notifierTask.cancel(false);
        this.stopAllPumps();
        this.setFinished();
    }

    public boolean isStarted() {
        return started;
    }

    public void cancel() {
        for (ScheduledFuture future : this.scheduledPumpFutures) {
            future.cancel(true);
        }
        if(this.finishTask != null) {
            this.finishTask.cancel(true);
        }
        this.stopAllPumps();
        if (this.scheduler.isShutdown()) {
            return;
        }
        this.scheduler.shutdown();
    }

    public StepProgress getProgress() {
        StepProgress stepProgress = new StepProgress();
        if(this.started) {
            stepProgress.setPercentCompleted(Math.min(100, (int) (((System.currentTimeMillis() - this.startTime) / ((double) (this.endTime - this.startTime))) * 100)));
        } else {
            stepProgress.setPercentCompleted(0);
        }
        stepProgress.setFinished(this.isFinished());
        return stepProgress;
    }

    private void stopAllPumps() {
        for(Pump pump : this.pumpPumpPhases.keySet()) {
            IGpioPin gpioPin = gpioController.getGpioPin(pump.getGpioPin());
            if(!gpioPin.isHigh()) {
                gpioPin.setHigh();
            }
        }
    }

    public long getRequiredPumpingTime() {
        return this.requiredWorkingTime;
    }
}
