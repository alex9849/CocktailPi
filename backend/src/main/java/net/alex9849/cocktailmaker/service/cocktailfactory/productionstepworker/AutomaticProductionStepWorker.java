package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.iface.IGpioController;
import net.alex9849.cocktailmaker.iface.IGpioPin;
import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStepIngredient;
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
    private final Set<PumpPhase> pumpPumpPhases;
    private final Set<Pump> updatedPumps;
    private final long requiredWorkingTime;
    private final ScheduledExecutorService scheduler;
    private Set<ScheduledFuture> scheduledPumpFutures;
    private ScheduledFuture finishTask;
    private ScheduledFuture notifierTask;

    private long startTime;
    private long endTime;

    /**
     *
     * @param pumps pumps is an output parameter! The attribute fillingLevelInMl will be decreased according to the recipe
     */
    public AutomaticProductionStepWorker(Set<Pump> pumps, List<ProductionStepIngredient> productionStepInstructions, int minimalPumpTime, int minimalBreakTime) {
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
        this.updatedPumps = pumpTimingStepCalculator.getUpdatedPumps();
        this.pumpPumpPhases = pumpTimingStepCalculator.getPumpPhases();
        this.requiredWorkingTime = pumpTimingStepCalculator.getLongestIngredientTime();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduledPumpFutures = new HashSet<>();
    }

    public void start() {
        super.start();
        this.startTime = System.currentTimeMillis();
        for (PumpPhase pumpPhase : this.pumpPumpPhases) {
            scheduledPumpFutures.add(scheduler.schedule(() -> {
                pumpPhase.getPump().setRunning(true);
                pumpPhase.setStarted();
            }, pumpPhase.getStartTime(), TimeUnit.MILLISECONDS));

            scheduledPumpFutures.add(scheduler.schedule(() -> {
                pumpPhase.getPump().setRunning(false);
                pumpPhase.setStopped();
            }, pumpPhase.getStopTime(), TimeUnit.MILLISECONDS));
        }
        long delayLastInstruction = this.scheduledPumpFutures.stream()
                .mapToLong(x -> x.getDelay(TimeUnit.MILLISECONDS)).max().getAsLong();

        this.finishTask = scheduler.schedule(this::onFinish, delayLastInstruction, TimeUnit.MILLISECONDS);
        this.notifierTask = this.scheduler.scheduleAtFixedRate(this::notifySubscribers, 1, 1, TimeUnit.SECONDS);
        this.endTime = System.currentTimeMillis() + delayLastInstruction;
        this.notifySubscribers();
    }

    public Set<Pump> getUpdatedPumps() {
        return updatedPumps;
    }

    private void onFinish() {
        this.scheduledPumpFutures.forEach(x -> x.cancel(true));
        this.notifierTask.cancel(false);
        this.stopAllPumps();
        this.setFinished();
    }

    public void cancel() {
        for (ScheduledFuture future : this.scheduledPumpFutures) {
            future.cancel(true);
        }
        if(this.finishTask != null) {
            this.finishTask.cancel(true);
        }
        this.stopAllPumps();
        if (!this.scheduler.isShutdown()) {
            this.scheduler.shutdown();
        }
    }

    public Map<Pump, Integer> getNotUsedLiquid() {
        Map<Pump, Integer> notUsedLiquidByPump = new HashMap<>();
        for(PumpPhase pumpPhase : this.pumpPumpPhases) {
            int notUsedLiquid = notUsedLiquidByPump.computeIfAbsent(pumpPhase.getPump(), p -> 0);
            notUsedLiquid += pumpPhase.getRemainingLiquidToPump();
            notUsedLiquidByPump.put(pumpPhase.getPump(), notUsedLiquid);
        }
        return notUsedLiquidByPump;
    }

    public StepProgress getProgress() {
        StepProgress stepProgress = new StepProgress();
        if(this.isStarted()) {
            stepProgress.setPercentCompleted(Math.min(100, (int) (((System.currentTimeMillis() - this.startTime) / ((double) (this.endTime - this.startTime))) * 100)));
        } else {
            stepProgress.setPercentCompleted(0);
        }
        stepProgress.setFinished(this.isFinished());
        return stepProgress;
    }

    private void stopAllPumps() {
        Set<Long> seenPumps = new HashSet<>();
        for(PumpPhase pumpPhase : this.pumpPumpPhases) {
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

    public long getRequiredPumpingTime() {
        return this.requiredWorkingTime;
    }
}
