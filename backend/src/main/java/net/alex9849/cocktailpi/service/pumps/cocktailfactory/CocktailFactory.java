package net.alex9849.cocktailpi.service.pumps.cocktailfactory;

import net.alex9849.cocktailpi.model.cocktail.CocktailProgress;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.recipe.FeasibleRecipe;
import net.alex9849.cocktailpi.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.ManualIngredient;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.model.recipe.productionstep.WrittenInstructionProductionStep;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.productionstepworker.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CocktailFactory {
    private final static int MINIMAL_PUMP_OPERATION_TIME_IN_MS = 500;
    private final static int MINIMAL_PUMP_BREAK_TIME_IN_MS = 500;

    private final List<Consumer<CocktailProgress>> subscribers = new ArrayList<>();
    private final List<AbstractProductionStepWorker> productionStepWorkers = new ArrayList<>();
    private AbstractProductionStepWorker currentProductionStepWorker = null;
    private Consumer<Set<Pump>> onRequestPumpPersist;
    private final Set<Pump> pumps;
    private final FeasibleRecipe feasibleRecipe;
    private final User user;
    private final Integer powerLimit;

    private CocktailProgress.State previousState = null;
    private CocktailProgress.State state = null;

    public CocktailFactory(FeasibleRecipe feasibleRecipe, User user, Set<Pump> pumps, Integer powerLimit, Consumer<Set<Pump>> onRequestPumpPersist) {
        this(feasibleRecipe, user, pumps, powerLimit);
        this.onRequestPumpPersist = onRequestPumpPersist;
    }

    public CocktailFactory(FeasibleRecipe feasibleRecipe, User user, Set<Pump> pumps, Integer powerLimit) {
        this.pumps = pumps;
        this.feasibleRecipe = feasibleRecipe;
        this.user = user;
        this.powerLimit = powerLimit;
        Map<Long, List<Pump>> pumpsByIngredientId = pumps.stream()
                .filter(x -> x.getCurrentIngredient() != null)
                .collect(Collectors.groupingBy(x -> x.getCurrentIngredient().getId()));

        Map<Pump, Integer> remainingFillingLevelByPump = new HashMap<>();
        for(ProductionStep pStep : feasibleRecipe.getFeasibleProductionSteps()) {
            this.productionStepWorkers.addAll(generateWorkers(pStep, pumpsByIngredientId, remainingFillingLevelByPump));
        }
        for (Map.Entry<Pump, Integer> entry : remainingFillingLevelByPump.entrySet()) {
            entry.getKey().setFillingLevelInMl(entry.getValue());
        }
        Set<Pump> usedPumps = this.getUpdatedPumps();
        if(!usedPumps.isEmpty()) {
            this.productionStepWorkers.addAll(0, generatePumpUpWorker(usedPumps));
        }

        Iterator<AbstractProductionStepWorker> workerIterator = this.productionStepWorkers.iterator();
        if(!workerIterator.hasNext()) {
            throw new IllegalArgumentException("Couldn't create ProductionStepWorkers from recipe!");
        }
        AbstractProductionStepWorker currentWorker = workerIterator.next();
        this.currentProductionStepWorker = currentWorker;
        while (workerIterator.hasNext()) {
            AbstractProductionStepWorker nextWorker = workerIterator.next();
            currentWorker.setOnFinishCallback(() -> {
                this.currentProductionStepWorker = nextWorker;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    new Thread(() -> this.cancelCocktail(true)).start();
                }
                nextWorker.start();
            });
            currentWorker = nextWorker;
        }
        currentWorker.setOnFinishCallback(this::onFinish);

        this.productionStepWorkers.forEach(x -> x.subscribeToProgress(this::onSubscriptionChange));
        setState(CocktailProgress.State.READY_TO_START);
    }

    private List<PumpUpProductionStepWorker> generatePumpUpWorker(Set<Pump> usedPumps) {
        if (powerLimit == null) {
            return List.of(new PumpUpProductionStepWorker(this, usedPumps));
        }
        int remainingPowerLimit = powerLimit;
        List<PumpUpProductionStepWorker> pumpUpWorkers = new ArrayList<>();
        Set<Pump> pumpUpPhasePumps = new HashSet<>();
        for(Pump pump : usedPumps) {
            if(pump.isPumpedUp()) {
                continue;
            }
            if(remainingPowerLimit < pump.getPowerConsumption()) {
                pumpUpWorkers.add(new PumpUpProductionStepWorker(this, pumpUpPhasePumps));
                pumpUpPhasePumps = new HashSet<>();
                remainingPowerLimit = powerLimit;
            }
            pumpUpPhasePumps.add(pump);
            remainingPowerLimit -= pump.getPowerConsumption();
        }
        if(!pumpUpPhasePumps.isEmpty()) {
            pumpUpWorkers.add(new PumpUpProductionStepWorker(this, pumpUpPhasePumps));
        }
        return pumpUpWorkers;
    }

    private List<AbstractProductionStepWorker> generateWorkers(ProductionStep pStep, Map<Long, List<Pump>> pumpsByIngredientId, Map<Pump, Integer> remainingFillingLevelByPump) {
        if(pStep instanceof AddIngredientsProductionStep) {
            return generateWorkers((AddIngredientsProductionStep) pStep, pumpsByIngredientId, remainingFillingLevelByPump);
        }
        if(pStep instanceof WrittenInstructionProductionStep wIPStep) {
            return List.of(new WrittenInstructionProductionStepWorker(this, wIPStep.getMessage()));
        }
        throw new IllegalStateException("ProductionStepType unknown!");
    }

    private List<AbstractProductionStepWorker> generateWorkers(AddIngredientsProductionStep pStep, Map<Long, List<Pump>> pumpsByIngredientId, Map<Pump, Integer> remainingFillingLevelByPump) {
        List<AbstractProductionStepWorker> workers = new ArrayList<>();
        List<ProductionStepIngredient> manualProductionSteps = new ArrayList<>();
        List<ProductionStepIngredient> automaticProductionSteps = new ArrayList<>();
        for(ProductionStepIngredient psi : pStep.getStepIngredients()) {
            if (psi.getAmount() < 1) {
                continue;
            }
            if (psi.getIngredient() instanceof ManualIngredient) {
                manualProductionSteps.add(psi);

            } else if (psi.getIngredient() instanceof AutomatedIngredient) {
                if(pumpsByIngredientId.containsKey(psi.getIngredient().getId())) {
                    automaticProductionSteps.add(psi);
                } else {
                    manualProductionSteps.add(psi);
                }
            }
            else {
                throw new IllegalStateException("IngredientType not implemented yet: "
                        + Objects.requireNonNull(psi.getIngredient()).getClass().getName());
            }
        }
        if(!manualProductionSteps.isEmpty()) {
            workers.add(new ManualProductionStepWorker(this, manualProductionSteps));
        }
        if(!automaticProductionSteps.isEmpty()) {
            List<PumpTimingStepCalculator> stepCalculators = AutomaticProductionStepWorker.splitStepByPowerLimit(pumps,
                    automaticProductionSteps, MINIMAL_PUMP_OPERATION_TIME_IN_MS, MINIMAL_PUMP_BREAK_TIME_IN_MS, remainingFillingLevelByPump, powerLimit);

            for(PumpTimingStepCalculator stepCalculator : stepCalculators) {
                workers.add(new AutomaticProductionStepWorker(this, stepCalculator));
            }
        }
        return workers;
    }

    public void requestPumpPersist(Set<Pump> pumps) {
        if(this.onRequestPumpPersist == null) {
            return;
        }
        this.onRequestPumpPersist.accept(pumps);
    }

    public Set<Pump> getUpdatedPumps() {
        return this.productionStepWorkers.stream()
                .filter(x -> x instanceof AbstractPumpingProductionStepWorker)
                .flatMap(x -> ((AbstractPumpingProductionStepWorker) x).getUsedPumps().stream())
                .collect(Collectors.toSet());
    }

    private Map<Pump, Integer> getNotUsedLiquid() {
        return this.productionStepWorkers.stream()
                .filter(x -> x instanceof AbstractPumpingProductionStepWorker)
                .flatMap(x -> ((AbstractPumpingProductionStepWorker) x).getNotUsedLiquid().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
    }

    private void onSubscriptionChange(StepProgress stepProgress) {
        if(stepProgress instanceof ManualStepProgress) {
            setState(CocktailProgress.State.MANUAL_INGREDIENT_ADD);
        } else if(stepProgress instanceof WrittenInstructionStepProgress) {
            setState(CocktailProgress.State.MANUAL_ACTION_REQUIRED);
        } else {
            setState(CocktailProgress.State.RUNNING);
        }
        this.notifySubscribers();
    }

    private Set<Ingredient> getNeededIngredientIds() {
        return new HashSet<>(this.feasibleRecipe.getFeasibleProductionSteps().stream()
                .filter(x -> x instanceof AddIngredientsProductionStep)
                .map(x -> (AddIngredientsProductionStep) x)
                .flatMap(x -> x.getStepIngredients().stream())
                .collect(Collectors.toMap(x -> x.getIngredient().getId(), ProductionStepIngredient::getIngredient, (a, b) -> a))
                .values());
    }

    private Set<Ingredient> getAvailableIngredientIds() {
        return new HashSet<>(this.pumps.stream()
                .collect(Collectors.toMap(x -> x.getCurrentIngredient().getId(), Pump::getCurrentIngredient, (a, b) -> a))
                .values());
    }

    public int getRecipeAmountOfLiquid() {
        return this.feasibleRecipe.getFeasibleProductionSteps().stream()
                .filter(x -> x instanceof AddIngredientsProductionStep)
                .map(x -> (AddIngredientsProductionStep) x)
                .flatMap(x -> x.getStepIngredients().stream())
                .mapToInt(ProductionStepIngredient::getAmount).sum();
    }

    public void makeCocktail() {
        if(this.state != CocktailProgress.State.READY_TO_START) {
            throw new IllegalStateException("Factory not ready to start!");
        }
        setState(CocktailProgress.State.RUNNING);
        this.requestPumpPersist(this.getUpdatedPumps());
        this.notifySubscribers();
        this.currentProductionStepWorker.start();
    }

    private void onFinish() {
        if(isFinished() || isCanceled()) {
            return;
        }
        setState(CocktailProgress.State.FINISHED);
        this.shutDown();
        this.requestPumpPersist(this.getUpdatedPumps());
        this.notifySubscribers();
    }

    public void cancelCocktail(boolean exceptional) {
        if(isFinished() || isCanceled()) {
            throw new IllegalStateException("Cocktail already completed!");
        }
        this.shutDown();
        if(exceptional) {
            setState(CocktailProgress.State.ERROR);
        } else {
            setState(CocktailProgress.State.CANCELLED);
        }

        Set<Pump> updatedPumps = this.getUpdatedPumps();
        Map<Pump, Integer> notUsedLiquidByPump = this.getNotUsedLiquid();
        for(Map.Entry<Pump, Integer> entry : notUsedLiquidByPump.entrySet()) {
            Pump pump = entry.getKey();
            Integer notUsedLiquid = entry.getValue();
            pump.setFillingLevelInMl(pump.getFillingLevelInMl() + notUsedLiquid);
        }
        updatedPumps.addAll(notUsedLiquidByPump.keySet());
        this.requestPumpPersist(updatedPumps);
        this.notifySubscribers();
    }

    public void continueProduction() {
        if(!(this.currentProductionStepWorker instanceof ManualFinishable)) {
            throw new IllegalStateException("No manual interaction required!");
        }
        ((ManualFinishable) this.currentProductionStepWorker).continueProduction();
    }

    private void shutDown() {
        for(AbstractProductionStepWorker worker : this.productionStepWorkers) {
            worker.cancel();
        }
    }

    private void setState(CocktailProgress.State state) {
        this.previousState = this.state;
        this.state = state;
    }

    public boolean isFinished() {
        return this.state == CocktailProgress.State.FINISHED;
    }

    public boolean isCanceled() {
        return this.state == CocktailProgress.State.CANCELLED
                ||this.state == CocktailProgress.State.ERROR;
    }

    public boolean isRunning() {
        return this.state == CocktailProgress.State.RUNNING;
    }

    public CocktailFactory subscribeProgress(Consumer<CocktailProgress> consumer) {
        this.subscribers.add(consumer);
        return this;
    }

    private void notifySubscribers() {
        for(Consumer<CocktailProgress> consumer : this.subscribers) {
            consumer.accept(getCocktailprogress());
        }
    }

    public CocktailProgress getCocktailprogress() {
        CocktailProgress cocktailprogress = new CocktailProgress();
        cocktailprogress.setUser(this.user);
        cocktailprogress.setRecipe(this.feasibleRecipe.getRecipe());
        cocktailprogress.setPreviousState(this.previousState);
        cocktailprogress.setState(this.state);
        cocktailprogress.setProgress(getProgressInPercent());

        if(this.currentProductionStepWorker instanceof ManualProductionStepWorker worker) {
            cocktailprogress.setCurrentIngredientsToAddManually(worker.getProgress().getIngredientsToBeAdded());
        }
        if(this.currentProductionStepWorker instanceof WrittenInstructionProductionStepWorker worker) {
            cocktailprogress.setWrittenInstruction(worker.getProgress().getMessage());
        }
        return cocktailprogress;
    }

    private int getProgressInPercent() {
        if(this.state == CocktailProgress.State.READY_TO_START) {
            return 0;
        }
        if(this.state == CocktailProgress.State.FINISHED) {
            return 100;
        }

        final long TIME_FOR_MANUAL_PROGRESS = TimeUnit.SECONDS.toMillis(15);
        long timeNeeded = 0;
        long timeElapsed = 0;
        for(AbstractProductionStepWorker worker : this.productionStepWorkers) {
            if(worker instanceof ManualProductionStepWorker || worker instanceof WrittenInstructionProductionStepWorker) {
                timeNeeded += TIME_FOR_MANUAL_PROGRESS;
                if(worker.isFinished()) {
                    timeElapsed += TIME_FOR_MANUAL_PROGRESS;
                }

            } else if (worker instanceof AbstractPumpingProductionStepWorker pumpingWorker) {
                timeNeeded += pumpingWorker.getRequiredPumpingTime();
                if(worker.isFinished()) {
                    timeElapsed += pumpingWorker.getRequiredPumpingTime();
                } else if (pumpingWorker.isStarted()) {
                    timeElapsed += Math.round((pumpingWorker.getProgress().getPercentCompleted() / 100d)
                            * pumpingWorker.getRequiredPumpingTime());
                }

            }  else {
                throw new IllegalStateException("Unknown worker type!");
            }
        }
        return Math.round((((float) timeElapsed) / timeNeeded) * 100);
    }

    public static Map<Ingredient, Integer> getNeededAmountNeededPerIngredient(FeasibleRecipe recipe) {
        return recipe.getFeasibleProductionSteps().stream()
                .filter(x -> x instanceof AddIngredientsProductionStep)
                .map(x -> (AddIngredientsProductionStep) x)
                .flatMap(x -> x.getStepIngredients().stream())
                .collect(Collectors.toMap(ProductionStepIngredient::getIngredient, ProductionStepIngredient::getAmount, Integer::sum));
    }
}
