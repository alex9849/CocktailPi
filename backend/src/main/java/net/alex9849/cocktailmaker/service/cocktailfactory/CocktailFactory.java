package net.alex9849.cocktailmaker.service.cocktailfactory;

import com.pi4j.io.gpio.RaspiPin;
import net.alex9849.cocktailmaker.iface.IGpioController;
import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ManualIngredient;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker.AbstractProductionStepWorker;
import net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker.AutomaticProductionStepWorker;
import net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker.ManualProductionStepWorker;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
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
    private int currentWorkerIndex = -1;
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
                }
                else if (recipeIngredient.getIngredient() instanceof ManualIngredient) {
                    if(pumpsByIngredientId.containsKey(recipeIngredient.getIngredient().getId())) {
                        automaticProductionSteps.add(recipeIngredient);
                    } else {
                        manualProductionSteps.add(recipeIngredient);
                    }
                }
                else {
                    throw new IllegalStateException("IgredientType not implemented yet!");
                }
                if(pumpsByIngredientId.containsKey(recipeIngredient.getIngredientId())) {

                }
            }
            if(!manualProductionSteps.isEmpty()) {
                this.productionStepWorkers.add(new ManualProductionStepWorker(manualProductionSteps));
            }
            if(!automaticProductionSteps.isEmpty()) {
                this.productionStepWorkers.add(new AutomaticProductionStepWorker(pumps, gpioController,
                        manualProductionSteps, MINIMAL_PUMP_OPERATION_TIME_IN_MS, MINIMAL_PUMP_BREAK_TIME_IN_MS));
            }
        }
        this.state = Cocktailprogress.State.READY_TO_START;
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

    private AbstractProductionStepWorker startNextWorker() {
        if(this.productionStepWorkers.size() <= this.currentWorkerIndex + 1) {
            return null;
        }
        this.currentWorkerIndex++;
        this.productionStepWorkers.get(this.currentWorkerIndex).start();
        return this.productionStepWorkers.get(this.currentWorkerIndex);
    }

    public void makeCocktail() {
        if(this.isRunning()) {
            throw new IllegalArgumentException("Already running!");
        }
        if(this.isDone) {
            throw new IllegalArgumentException("Did already run!");
        }

        this.currentProductionStepWorker = startNextWorker();
        if(this.currentProductionStepWorker == null) {
            this.onFinish();
            return;
        }
        this.notifySubscribers();

        for(long i = 0; i <= this.preparationTime; i += 1000) {
            scheduledFutures.add(scheduler.schedule(() -> {
                this.updateCocktailProgress();
                this.notifySubscribers();
            }, i, TimeUnit.MILLISECONDS));
        }
        scheduledFutures.add(scheduler.schedule(() -> {
            this.setDone();
            this.notifySubscribers();
        }, this.preparationTime, TimeUnit.MILLISECONDS));

        this.cocktailprogress = new Cocktailprogress();
        this.cocktailprogress.setUser(this.user);
        this.cocktailprogress.setRecipe(this.recipe);
        this.cocktailprogress.setCanceled(false);
        this.updateCocktailProgress();
        this.notifySubscribers();
        return cocktailprogress;
    }

    private void onFinish() {

    }

    public void shutDown() {
        if(this.scheduler.isShutdown()) {
            return;
        }
        for(ScheduledFuture future : this.scheduledFutures) {
            future.cancel(true);
        }
        this.scheduler.shutdown();
        for(Pump pump : this.pumpTimings.keySet()) {
            gpioController.provideGpioPin(RaspiPin.getPinByAddress(pump.getGpioPin())).setHigh();
        }
        this.gpioController.shutdown();
    }

    public void cancelCocktail() {
        if(isDone()) {
            throw new IllegalStateException("Cocktail already done!");
        }
        this.shutDown();
        this.isCanceled = true;
        this.updateCocktailProgress();
        this.notifySubscribers();
    }

    public void setDone() {
        if(!this.isDone) {
            this.isDone = true;
            this.updateCocktailProgress();
        }
    }

    public boolean isDone() {
        return isDone;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public boolean isRunning() {
        if(this.scheduledFutures.stream().anyMatch(x -> !x.isDone())) {
            return true;
        }
        return false;
    }

    private void updateCocktailProgress() {
        if(this.cocktailprogress == null) {
            return;
        }
        this.cocktailprogress.setCanceled(this.isCanceled());
        if(!this.isCanceled()) {
            int intProgress = 100;
            if(!this.isDone()) {
                long runningSince = System.currentTimeMillis() - this.startTime;
                double progress = ( runningSince / (double) this.preparationTime) * 100;
                intProgress = (int) Math.min(progress, 100);
            }
            this.cocktailprogress.setDone(this.isDone());
            this.cocktailprogress.setProgress(intProgress);
        }
        this.setChanged();
    }

    public CocktailFactory subscribeProgress(Consumer<Cocktailprogress> consumer) {
        this.subscribers.add(consumer);
        return this;
    }

    public void notifySubscribers() {
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
            cocktailprogress.setCurrentRequiredAction(worker.getProgress().getIngredientToBeAdded());
        }

    }

    private int getProgressInPercent() {
        if(this.state == Cocktailprogress.State.READY_TO_START) {
            return 0;
        }
        if(this.state == Cocktailprogress.State.FINISHED) {
            return 100;
        }
        int mlToAddManually = this.productionStepWorkers.stream()
                .filter(x -> x instanceof ManualProductionStepWorker)
                .mapToInt(x -> x.getAmountToFill())
                .sum();
        int mlToAddAutomatically = this.productionStepWorkers.stream()
                .filter(x -> x instanceof AutomaticProductionStepWorker)
                .mapToInt(x -> x.getAmountToFill())
                .sum();
        int mlToFill = mlToAddAutomatically + mlToAddManually;

        //TODO

    }


    public static Recipe transformToAmountOfLiquid(Recipe recipe, int wantedAmountOfLiquid) {
        int currentLiquidAmount = recipe.getRecipeIngredients().stream()
                .filter(x -> x.getIngredient().isAddToVolume())
                .mapToInt(x -> x.getAmount()).sum();
        double multiplier = wantedAmountOfLiquid / ((double) currentLiquidAmount);

        for(RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
            recipeIngredient.setAmount((int) (recipeIngredient.getAmount() * multiplier));
        }
        return recipe;
    }
}
