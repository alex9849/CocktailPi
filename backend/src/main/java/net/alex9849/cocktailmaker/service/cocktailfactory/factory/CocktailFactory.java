package net.alex9849.cocktailmaker.service.cocktailfactory.factory;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import net.alex9849.cocktailmaker.model.user.User;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CocktailFactory extends Observable {
    private final int MINIMAL_PUMP_OPERATION_TIME_IN_MS = 500;
    private final int MINIMAL_PUMP_BREAK_TIME_IN_MS = 500;

    private List<List<RecipeIngredient>> productionSteps = new ArrayList<>();
    private Map<Long, Pump> ingredientIdToPumpMap;
    private Recipe recipe;
    private Collection<Pump> pumps;
    private User user;
    private boolean done = false;
    private Map<Pump, List<PumpPhase>> pumpTimings;
    private long preparationTime;
    private int requestedAmount;
    //Valid after start
    private Cocktailprogress cocktailprogress;
    private long startTime;
    //Scheduling stuff
    private ScheduledExecutorService scheduler;
    private Set<ScheduledFuture> scheduledFutures = new HashSet<>();

    public CocktailFactory(Recipe recipe, User user, Collection<Pump> pumps, int amount) {
        if(amount < 50 || amount > 1000) {
            throw new IllegalArgumentException("Amount needs to be between 50 and 1000 ml");
        }
        this.recipe = recipe;
        this.requestedAmount = amount;
        this.user = user;
        this.pumps = pumps;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.ingredientIdToPumpMap = pumps.stream().filter(x -> x.getCurrentIngredient() != null).collect(Collectors.toMap(x -> x.getCurrentIngredient().getId(), x-> x));
        Map<Integer, List<RecipeIngredient>> productionsStepMap = recipe.getRecipeIngredients().stream().collect(Collectors.groupingBy(x -> x.getId().getProductionStep()));
        List<Integer> steps = new ArrayList<>(productionsStepMap.keySet());
        steps.sort(Comparator.comparingInt(x -> x));
        for(Integer step : steps) {
            this.productionSteps.add(productionsStepMap.get(step));
        }
        if(!areEnoughPumpsAvailable()) {
            throw new IllegalArgumentException("Not enough pumps");
        }
        if(!doPumpsHaveAllIngredients()) {
            throw new IllegalArgumentException("Bad ingredient allocation");
        }
        this.calcPumpTimings();
    }

    private List<Long> getNeededIngredientIds() {
        return this.recipe.getRecipeIngredients().stream().map(x -> x.getIngredient().getId()).collect(Collectors.toList());
    }

    private List<Long> getAvailableIngredientIds() {
        return ingredientIdToPumpMap.values().stream().filter(x -> x.getCurrentIngredient() != null).map(x -> x.getCurrentIngredient().getId()).collect(Collectors.toList());
    }

    public boolean areEnoughPumpsAvailable() {
        return getNeededIngredientIds().size() <= pumps.size();
    }

    public boolean doPumpsHaveAllIngredients() {
        List<Long> neededIngredientIds = getNeededIngredientIds();
        List<Long> availableIngredientIds = getAvailableIngredientIds();
        if(neededIngredientIds.size() > availableIngredientIds.size()) {
            return false;
        }
        neededIngredientIds.removeAll(availableIngredientIds);
        return neededIngredientIds.isEmpty();
    }

    public int getRecipeAmountOfLiquid() {
        return this.recipe.getRecipeIngredients().stream().mapToInt(RecipeIngredient::getAmount).sum();
    }

    private void calcPumpTimings() {
        int currentOffset = 0;
        int recipeAmountOfLiquid = getRecipeAmountOfLiquid();
        Map<Pump, List<PumpPhase>> recipePumpTimings = new HashMap<>();

        int i = 0;
        for(List<RecipeIngredient> productionStep : this.productionSteps) {
            boolean isLast = i == this.productionSteps.size() - 1;
            List<Map.Entry<Pump, Integer>> stepPumpTime = new ArrayList<>();
            if(productionStep.size() == 0) {
                continue;
            }
            //Calculate how long each pump needs to run
            for(RecipeIngredient ingredient : productionStep) {
                Pump ingPump = this.ingredientIdToPumpMap.get(ingredient.getIngredient().getId());
                double pumpTime = ingPump.getTimePerClInMs() * (ingredient.getAmount() / 10d) * this.requestedAmount / recipeAmountOfLiquid;
                stepPumpTime.add(new AbstractMap.SimpleEntry<>(ingPump, (int) pumpTime));
            }
            //Sort pumps by the time they need to run in descending order
            Comparator<Map.Entry<Pump, Integer>> comparator = Map.Entry.comparingByValue(Comparator.reverseOrder());
            stepPumpTime.sort(comparator);
            //stepPumpTimes Size is at least 1
            Map.Entry<Pump, Integer> longestPumptime = stepPumpTime.get(0);
            stepPumpTime.remove(0);
            Map<Pump, Integer> otherPumpTimings = new HashMap<>();
            stepPumpTime.forEach(x -> otherPumpTimings.put(x.getKey(), x.getValue()));
            PumpTimingStepCalculator pumpTimingStepCalculator = new PumpTimingStepCalculator(currentOffset, longestPumptime.getValue(),
                    longestPumptime.getKey(), otherPumpTimings, this.MINIMAL_PUMP_OPERATION_TIME_IN_MS, this.MINIMAL_PUMP_BREAK_TIME_IN_MS);
            currentOffset += longestPumptime.getValue();
            Map<Pump, List<PumpPhase>> stepPumpPhases = pumpTimingStepCalculator.getPumpPhases();
            for(Map.Entry<Pump, List<PumpPhase>> pumpPhases : stepPumpPhases.entrySet()) {
                if(recipePumpTimings.putIfAbsent(pumpPhases.getKey(), pumpPhases.getValue()) != null) {
                    recipePumpTimings.get(pumpPhases.getKey()).addAll(pumpPhases.getValue());
                }
            }
            if(isLast) {
                this.preparationTime = stepPumpPhases.entrySet().stream().mapToInt(x -> x.getValue().stream().mapToInt(PumpPhase::getStopTime).max().getAsInt()).max().getAsInt();
            }
            i++;
        }
        this.pumpTimings = recipePumpTimings;
    }

    public Cocktailprogress makeCocktail() {
        if(this.isRunning()) {
            throw new IllegalArgumentException("Already running!");
        }
        if(this.done) {
            throw new IllegalArgumentException("Did already run!");
        }
        this.startTime = System.currentTimeMillis();
        for(Map.Entry<Pump, List<PumpPhase>> pumpPumpPhases : pumpTimings.entrySet()) {
            for(PumpPhase pumpPhase : pumpPumpPhases.getValue()) {
                scheduledFutures.add(scheduler.schedule(() -> {
                    System.out.println(pumpPhase.getPump().getGpioPin() + " started!");
                }, pumpPhase.getStartTime(), TimeUnit.MILLISECONDS));
                scheduledFutures.add(scheduler.schedule(() -> {
                    System.out.println(pumpPhase.getPump().getGpioPin() + " stopped!");
                }, pumpPhase.getStopTime(), TimeUnit.MILLISECONDS));
            }
            for(long i = 0; i <= this.preparationTime; i += 1000) {
                scheduledFutures.add(scheduler.schedule(() -> {
                    this.updateCocktailProgress();
                    this.notifyObservers();
                }, i, TimeUnit.MILLISECONDS));
            }
            scheduledFutures.add(scheduler.schedule(() -> {
                this.updateCocktailProgress();
                this.notifyObservers();
            }, this.preparationTime, TimeUnit.MILLISECONDS));
        }
        this.cocktailprogress = new Cocktailprogress();
        this.cocktailprogress.setUser(this.user);
        this.cocktailprogress.setRecipe(this.recipe);
        this.cocktailprogress.setCanceled(false);
        this.updateCocktailProgress();
        this.notifyObservers();
        return cocktailprogress;
    }

    public void shutDown() {
        if(this.scheduler.isShutdown()) {
            return;
        }
        for(ScheduledFuture future : this.scheduledFutures) {
            future.cancel(true);
        }
        this.scheduler.shutdown();
        for(Pump pump : this.ingredientIdToPumpMap.values()) {
            //TODO turn off all pumps!
        }
        this.done = true;
    }

    public void cancelCocktail() {
        this.shutDown();
        this.cocktailprogress.setCanceled(true);
        this.notifyObservers();
    }

    public boolean isDone() {
        return done;
    }

    public boolean isRunning() {
        if(this.scheduledFutures.stream().anyMatch(x -> !x.isDone())) {
            return true;
        }
        return false;
    }

    private void updateCocktailProgress() {
        long runningSince = System.currentTimeMillis() - this.startTime;
        double progress = ( runningSince / (double) this.preparationTime) * 100;
        progress = Math.min(progress, 100);
        this.cocktailprogress.setProgress((int) progress);
    }


    public void notifyObservers() {
        this.notifyObservers(this.cocktailprogress);
    }

    public Cocktailprogress getCocktailprogress() {
        return this.cocktailprogress;
    }
}
