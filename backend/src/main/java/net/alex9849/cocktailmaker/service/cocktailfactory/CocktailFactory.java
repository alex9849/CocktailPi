package net.alex9849.cocktailmaker.service.cocktailfactory;

import net.alex9849.cocktailmaker.iface.IGpioController;
import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.cocktail.CocktailProgress;
import net.alex9849.cocktailmaker.model.recipe.*;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.ManualIngredient;
import net.alex9849.cocktailmaker.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailmaker.model.recipe.productionstep.WrittenInstructionProductionStep;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CocktailFactory {
    private final int MINIMAL_PUMP_OPERATION_TIME_IN_MS = 500;
    private final int MINIMAL_PUMP_BREAK_TIME_IN_MS = 500;

    private final List<Consumer<CocktailProgress>> subscribers = new ArrayList<>();
    private final List<AbstractProductionStepWorker> productionStepWorkers = new ArrayList<>();
    private AbstractProductionStepWorker currentProductionStepWorker = null;
    private final Set<Pump> pumps;
    private final IGpioController gpioController;
    private final FeasibleRecipe feasibleRecipe;
    private final User user;

    private int requestedAmount;
    private CocktailProgress cocktailprogress;
    private CocktailProgress.State previousState = null;
    private CocktailProgress.State state = null;

    /**
     * @param feasibleRecipe the recipe constisting only of productionsteps that contain ManualIngredients and AutomatedIngredients.
     * @param pumps pumps is an output parameter! The attribute fillingLevelInMl will be decreased according to the recipe.
     */
    public CocktailFactory(FeasibleRecipe feasibleRecipe, User user, Set<Pump> pumps, IGpioController gpioController) {
        this.pumps = pumps;
        this.gpioController = gpioController;
        this.feasibleRecipe = feasibleRecipe;
        this.user = user;
        Map<Long, List<Pump>> pumpsByIngredientId = pumps.stream()
                .filter(x -> x.getCurrentIngredient() != null)
                .collect(Collectors.groupingBy(x -> x.getCurrentIngredient().getId()));

        for(ProductionStep pStep : feasibleRecipe.getFeasibleProductionSteps()) {
            this.productionStepWorkers.addAll(generateWorkers(pStep, pumpsByIngredientId));
        }
        Iterator<AbstractProductionStepWorker> workerIterator = this.productionStepWorkers.iterator();
        if(!workerIterator.hasNext()) {
            throw new IllegalArgumentException("Cound't create ProductionStepWorkers from recipe!");
        }
        AbstractProductionStepWorker currentWorker = workerIterator.next();
        this.currentProductionStepWorker = currentWorker;
        while (workerIterator.hasNext()) {
            AbstractProductionStepWorker nextWorker = workerIterator.next();
            currentWorker.setOnFinishCallback(() -> {
                this.currentProductionStepWorker = nextWorker;
                nextWorker.start();
            });
            currentWorker = nextWorker;
        }
        currentWorker.setOnFinishCallback(() -> this.onFinish());

        this.productionStepWorkers.forEach(x -> x.subscribeToProgress(this::onSubscriptionChange));
        setState(CocktailProgress.State.READY_TO_START);
    }

    private List<AbstractProductionStepWorker> generateWorkers(ProductionStep pStep, Map<Long, List<Pump>> pumpsByIngredientId) {
        if(pStep instanceof AddIngredientsProductionStep) {
            return generateWorkers((AddIngredientsProductionStep) pStep, pumpsByIngredientId);
        }
        if(pStep instanceof WrittenInstructionProductionStep) {
            WrittenInstructionProductionStep wIPStep = (WrittenInstructionProductionStep) pStep;
            return Arrays.asList(new WrittenInstructionProductionStepWorker(wIPStep.getMessage()));
        }
        throw new IllegalStateException("ProductionStepType unknown!");
    }

    private List<AbstractProductionStepWorker> generateWorkers(AddIngredientsProductionStep pStep, Map<Long, List<Pump>> pumpsByIngredientId) {
        List<AbstractProductionStepWorker> workers = new ArrayList<>();
        List<ProductionStepIngredient> manualProductionSteps = new ArrayList<>();
        List<ProductionStepIngredient> automaticProductionSteps = new ArrayList<>();
        for(ProductionStepIngredient psi : pStep.getStepIngredients()) {
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
                throw new IllegalStateException("IgredientType not implemented yet: "
                        + Objects.requireNonNull(psi.getIngredient()).getClass().getName());
            }
        }
        if(!manualProductionSteps.isEmpty()) {
            workers.add(new ManualProductionStepWorker(manualProductionSteps));
        }
        if(!automaticProductionSteps.isEmpty()) {
            workers.add(new AutomaticProductionStepWorker(pumps, gpioController,
                    automaticProductionSteps, MINIMAL_PUMP_OPERATION_TIME_IN_MS, MINIMAL_PUMP_BREAK_TIME_IN_MS));
        }
        return workers;
    }

    public Set<Pump> getUsedPumps() {
        return this.productionStepWorkers.stream()
                .filter(x -> x instanceof AutomaticProductionStepWorker)
                .flatMap(x -> ((AutomaticProductionStepWorker) x).getUsedPumps().stream())
                .collect(Collectors.toSet());
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
        return new HashSet<Ingredient>(this.feasibleRecipe.getFeasibleProductionSteps().stream()
                .filter(x -> x instanceof AddIngredientsProductionStep)
                .map(x -> (AddIngredientsProductionStep) x)
                .flatMap(x -> x.getStepIngredients().stream())
                .collect(Collectors.toMap(x -> x.getIngredient().getId(), x -> x.getIngredient(), (a, b) -> a))
                .values());
    }

    private Set<Ingredient> getAvailableIngredientIds() {
        return new HashSet<Ingredient>(this.pumps.stream()
                .collect(Collectors.toMap(x -> x.getCurrentIngredient().getId(), x -> x.getCurrentIngredient(), (a, b) -> a))
                .values());
    }

    public Set<Ingredient> getNonAutomaticIngredients() {
        Set<Ingredient> neededIngredientIds = getNeededIngredientIds();
        Set<Ingredient> availableIngredientIds = getAvailableIngredientIds();
        if(neededIngredientIds.size() > availableIngredientIds.size()) {
            return new HashSet<>();
        }
        return neededIngredientIds.stream()
                .filter(x -> availableIngredientIds.stream().noneMatch(y -> x.getId() == y.getId()))
                .collect(Collectors.toSet());
    }

    public int getRecipeAmountOfLiquid() {
        return this.feasibleRecipe.getFeasibleProductionSteps().stream()
                .filter(x -> x instanceof AddIngredientsProductionStep)
                .map(x -> (AddIngredientsProductionStep) x)
                .flatMap(x -> x.getStepIngredients().stream())
                .mapToInt(ProductionStepIngredient::getAmount).sum();
    }

    private void handleWorkerNotification(AbstractProductionStepWorker worker, StepProgress stepProgress) {
        this.notifySubscribers();
    }

    public void makeCocktail() {
        if(this.state != CocktailProgress.State.READY_TO_START) {
            throw new IllegalStateException("Factory not ready to start!");
        }
        setState(CocktailProgress.State.RUNNING);
        this.cocktailprogress = new CocktailProgress();
        this.notifySubscribers();
        this.currentProductionStepWorker.start();
    }

    private void onFinish() {
        if(isFinished() || isCanceled()) {
            return;
        }
        setState(CocktailProgress.State.FINISHED);
        this.shutDown();
        this.notifySubscribers();
    }

    public void cancelCocktail() {
        if(isFinished() || isCanceled()) {
            throw new IllegalStateException("Cocktail already done!");
        }
        this.shutDown();
        setState(CocktailProgress.State.CANCELLED);
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
            if(worker instanceof AutomaticProductionStepWorker) {
                AutomaticProductionStepWorker automaticWorker = (AutomaticProductionStepWorker) worker;
                automaticWorker.cancel();
            }
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
        return this.state == CocktailProgress.State.CANCELLED;
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

        if(this.currentProductionStepWorker instanceof ManualProductionStepWorker) {
            ManualProductionStepWorker worker = (ManualProductionStepWorker) this.currentProductionStepWorker;
            cocktailprogress.setCurrentIngredientsToAddManually(worker.getProgress().getIngredientsToBeAdded());
        }
        if(this.currentProductionStepWorker instanceof WrittenInstructionProductionStepWorker) {
            WrittenInstructionProductionStepWorker worker = (WrittenInstructionProductionStepWorker) this.currentProductionStepWorker;
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
            if(worker instanceof ManualProductionStepWorker) {
                timeNeeded += TIME_FOR_MANUAL_PROGRESS;
                if(worker.isFinished()) {
                    timeElapsed += TIME_FOR_MANUAL_PROGRESS;
                }
            } else if (worker instanceof AutomaticProductionStepWorker) {
                AutomaticProductionStepWorker automaticWorker = (AutomaticProductionStepWorker) worker;
                timeNeeded += automaticWorker.getRequiredPumpingTime();
                if(worker.isFinished()) {
                    timeElapsed += automaticWorker.getRequiredPumpingTime();
                } else if (automaticWorker.isStarted()) {
                    timeElapsed += Math.round((automaticWorker.getProgress().getPercentCompleted() / 100d)
                            * automaticWorker.getRequiredPumpingTime());
                }
            } else if (worker instanceof WrittenInstructionProductionStepWorker) {
                timeNeeded += TIME_FOR_MANUAL_PROGRESS;
                if(worker.isFinished()) {
                    timeElapsed += TIME_FOR_MANUAL_PROGRESS;
                }
            } else {
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
                .collect(Collectors.toMap(x -> x.getIngredient(), x -> x.getAmount(), (a, b) -> a + b));
    }


    /**
     * Recalculates the amount of liquid for all ingredients in order to sum up to the total wanted amount of liquid
     * @param recipe the recipe
     * @param wantedAmountOfLiquid the wanted amount of liquid
     * @return the same instance of the recipe object that got passed in, but with recalculated
     * amount of liquids for all ingredients
     */
    public static Recipe transformToAmountOfLiquid(Recipe recipe, int wantedAmountOfLiquid) {
        int liquidAmountScaled = recipe.getProductionSteps().stream()
                .filter(x -> x instanceof AddIngredientsProductionStep)
                .map(x -> (AddIngredientsProductionStep) x)
                .flatMap(x -> x.getStepIngredients().stream())
                .filter(x -> x.getIngredient().getUnit() == Ingredient.Unit.MILLILITER)
                .filter(x -> x.isScale())
                .mapToInt(x -> x.getAmount()).sum();
        int liquidAmountUnscaled = recipe.getProductionSteps().stream()
                .filter(x -> x instanceof AddIngredientsProductionStep)
                .map(x -> (AddIngredientsProductionStep) x)
                .flatMap(x -> x.getStepIngredients().stream())
                .filter(x -> x.getIngredient().getUnit() == Ingredient.Unit.MILLILITER)
                .filter(x -> !x.isScale())
                .mapToInt(x -> x.getAmount()).sum();
        int liquidAmountToBeScaledTo = wantedAmountOfLiquid - liquidAmountUnscaled;
        if(liquidAmountScaled <= 0) {
            return recipe;
        }
        double multiplier;
        if(liquidAmountToBeScaledTo < 0) {
            multiplier = 0;
        } else {
            multiplier = wantedAmountOfLiquid / ((double) liquidAmountScaled);
        }

        for(ProductionStep pStep : recipe.getProductionSteps()) {
            if(pStep instanceof AddIngredientsProductionStep) {
                AddIngredientsProductionStep addIPStep = (AddIngredientsProductionStep) pStep;
                for(ProductionStepIngredient psi : addIPStep.getStepIngredients()) {
                    if(psi.isScale()) {
                        psi.setAmount((int) (psi.getAmount() * multiplier));
                    }
                }
            }
        }
        return recipe;
    }
}
