package net.alex9849.cocktailmaker.service.cocktailfactory;

import com.pi4j.io.gpio.RaspiPin;
import net.alex9849.cocktailmaker.iface.IGpioController;
import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.model.recipe.*;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CocktailFactory {
    private final int MINIMAL_PUMP_OPERATION_TIME_IN_MS = 500;
    private final int MINIMAL_PUMP_BREAK_TIME_IN_MS = 500;

    private final List<Consumer<Cocktailprogress>> subscribers = new ArrayList<>();
    private final List<AbstractProductionStepWorker> productionStepWorkers = new ArrayList<>();
    private AbstractProductionStepWorker currentProductionStepWorker = null;
    private final Set<Pump> pumps;
    private final IGpioController gpioController;
    private final Recipe recipe;
    private final User user;

    private int requestedAmount;
    private Cocktailprogress cocktailprogress;
    private Cocktailprogress.State state;

    public CocktailFactory(Recipe recipe, User user, Set<Pump> pumps, IGpioController gpioController) {
        this.pumps = pumps;
        this.gpioController = gpioController;
        this.recipe = recipe;
        this.user = user;
        Map<Long, Pump> pumpsByIngredientId = pumps.stream()
                .filter(x -> x.getCurrentIngredient() != null)
                .collect(Collectors.toMap(x -> x.getCurrentIngredient().getId(), x -> x, (a, b) -> a));
        Map<Long, List<RecipeIngredient>> recipeIngredientByStep = recipe.getRecipeIngredients().stream()
                .collect(Collectors.groupingBy(x -> x.getProductionStep()));
        List<Long> sortedProductionsSteps = recipeIngredientByStep.keySet().stream().sorted().collect(Collectors.toList());
        List<List<RecipeIngredient>> productionSteps = new ArrayList<>();

        for(Long prodstep : sortedProductionsSteps) {
            List<RecipeIngredient> manualProductionSteps = new ArrayList<>();
            List<RecipeIngredient> automaticProductionSteps = new ArrayList<>();
            for(RecipeIngredient recipeIngredient : recipeIngredientByStep.get(prodstep)) {
                if (recipeIngredient.getIngredient() instanceof ManualIngredient) {
                    manualProductionSteps.add(recipeIngredient);

                } else if (recipeIngredient.getIngredient() instanceof AutomatedIngredient) {
                    if(pumpsByIngredientId.containsKey(recipeIngredient.getIngredient().getId())) {
                        automaticProductionSteps.add(recipeIngredient);
                    } else {
                        manualProductionSteps.add(recipeIngredient);
                    }
                }
                else {
                    throw new IllegalStateException("IgredientType not implemented yet: "
                            + Objects.requireNonNull(recipeIngredient.getIngredient()).getClass().getName());
                }
            }
            if(!manualProductionSteps.isEmpty()) {
                this.productionStepWorkers.add(new ManualProductionStepWorker(manualProductionSteps));
            }
            if(!automaticProductionSteps.isEmpty()) {
                this.productionStepWorkers.add(new AutomaticProductionStepWorker(pumps, gpioController,
                        automaticProductionSteps, MINIMAL_PUMP_OPERATION_TIME_IN_MS, MINIMAL_PUMP_BREAK_TIME_IN_MS));
            }
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
        this.state = Cocktailprogress.State.READY_TO_START;
    }

    private void onSubscriptionChange(StepProgress stepProgress) {
        if(stepProgress instanceof ManualStepProgress) {
            this.state = Cocktailprogress.State.MANUAL_ACTION_REQUIRED;
        } else {
            this.state = Cocktailprogress.State.RUNNING;
        }
        this.notifySubscribers();
    }

    private Set<Ingredient> getNeededIngredientIds() {
        return new HashSet<Ingredient>(this.recipe.getRecipeIngredients().stream()
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
        return this.recipe.getRecipeIngredients().stream().mapToInt(RecipeIngredient::getAmount).sum();
    }

    private void handleWorkerNotification(AbstractProductionStepWorker worker, StepProgress stepProgress) {
        this.notifySubscribers();
    }

    public void makeCocktail() {
        if(this.state != Cocktailprogress.State.READY_TO_START) {
            throw new IllegalStateException("Factory not ready to start!");
        }
        this.state = Cocktailprogress.State.RUNNING;
        this.cocktailprogress = new Cocktailprogress();
        this.currentProductionStepWorker.start();
    }

    private void onFinish() {
        if(isFinished() || isCanceled()) {
            return;
        }
        this.state = Cocktailprogress.State.FINISHED;
        this.shutDown();
        this.notifySubscribers();
    }

    public void cancelCocktail() {
        if(isFinished() || isCanceled()) {
            throw new IllegalStateException("Cocktail already done!");
        }
        this.shutDown();
        this.state = Cocktailprogress.State.CANCELLED;
        this.notifySubscribers();
    }

    public void continueProduction() {
        if(!(this.currentProductionStepWorker instanceof ManualProductionStepWorker)) {
            throw new IllegalStateException("No manual interaction required!");
        }
        ((ManualProductionStepWorker) this.currentProductionStepWorker).continueProduction();
    }

    private void shutDown() {
        for(AbstractProductionStepWorker worker : this.productionStepWorkers) {
            if(worker instanceof AutomaticProductionStepWorker) {
                AutomaticProductionStepWorker automaticWorker = (AutomaticProductionStepWorker) worker;
                automaticWorker.cancel();
            }
        }
        for(Pump pump : this.pumps) {
            gpioController.provideGpioPin(RaspiPin.getPinByAddress(pump.getGpioPin())).setHigh();
        }
        this.gpioController.shutdown();
    }

    public boolean isFinished() {
        return this.state == Cocktailprogress.State.FINISHED;
    }

    public boolean isCanceled() {
        return this.state == Cocktailprogress.State.CANCELLED;
    }

    public boolean isRunning() {
        return this.state == Cocktailprogress.State.RUNNING;
    }

    public CocktailFactory subscribeProgress(Consumer<Cocktailprogress> consumer) {
        this.subscribers.add(consumer);
        return this;
    }

    private void notifySubscribers() {
        for(Consumer<Cocktailprogress> consumer : this.subscribers) {
            consumer.accept(getCocktailprogress());
        }
    }

    public Cocktailprogress getCocktailprogress() {
        Cocktailprogress cocktailprogress = new Cocktailprogress();
        cocktailprogress.setUser(this.user);
        cocktailprogress.setRecipe(this.recipe);
        cocktailprogress.setState(this.state);
        cocktailprogress.setProgress(getProgressInPercent());

        if(this.currentProductionStepWorker instanceof ManualProductionStepWorker) {
            ManualProductionStepWorker worker = (ManualProductionStepWorker) this.currentProductionStepWorker;
            cocktailprogress.setCurrentIngredientsToAddManually(worker.getProgress().getIngredientsToBeAdded());
        }
        return cocktailprogress;
    }

    private int getProgressInPercent() {
        if(this.state == Cocktailprogress.State.READY_TO_START) {
            return 0;
        }
        if(this.state == Cocktailprogress.State.FINISHED) {
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
            }
        }
        return Math.round((((float) timeElapsed) / timeNeeded) * 100);
    }


    /**
     * Recalculates the amount of liquid for all ingredients in order to sum up to the total wanted amount of liquid
     * @param recipe the recipe
     * @param wantedAmountOfLiquid the wanted amount of liquid
     * @return the same instance of the recipe object that got passed in, but with recalculated
     * amount of liquids for all ingredients
     */
    public static Recipe transformToAmountOfLiquid(Recipe recipe, int wantedAmountOfLiquid) {
        int liquidAmountScaled = recipe.getRecipeIngredients().stream()
                .filter(x -> x.getIngredient().getUnit() == Ingredient.Unit.MILLILITER)
                .filter(x -> x.isScale())
                .mapToInt(x -> x.getAmount()).sum();
        int liquidAmountUnscaled = recipe.getRecipeIngredients().stream()
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

        for(RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
            if(recipeIngredient.isScale()) {
                recipeIngredient.setAmount((int) (recipeIngredient.getAmount() * multiplier));
            }
        }
        return recipe;
    }
}
