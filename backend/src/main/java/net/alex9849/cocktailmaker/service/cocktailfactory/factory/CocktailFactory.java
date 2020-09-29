package net.alex9849.cocktailmaker.service.cocktailfactory.factory;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.model.exception.BadIngredientAllocation;
import net.alex9849.cocktailmaker.model.exception.NotEnoughPumpsException;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.model.util.Observable;
import net.alex9849.cocktailmaker.model.util.Observer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public class CocktailFactory implements Observable<Cocktailprogress> {
    private final int MINIMAL_PUMP_OPERATION_TIME_IN_MS = 500;
    private final int MINIMAL_PUMP_BREAK_TIME_IN_MS = 500;

    private Set<Observer<Cocktailprogress>> observers = new HashSet<>();
    private List<List<RecipeIngredient>> productionSteps = new ArrayList<>();
    private Map<Long, Pump> ingredientIdToPumpMap;
    private Recipe recipe;
    private Collection<Pump> pumps;
    private User user;
    private boolean done = false;
    private Map<Pump, List<PumpPhase>> pumpTimings;
    private long preparationTime;
    //Valid after start
    private Cocktailprogress cocktailprogress;
    private long startTime;
    //Scheduling stuff
    @Autowired
    private ThreadPoolTaskScheduler scheduler;
    private Set<ScheduledFuture> scheduledFutures = new HashSet<>();

    public CocktailFactory(Recipe recipe, User user, Collection<Pump> pumps) {
        this.recipe = recipe;
        this.user = user;
        this.pumps = pumps;
        this.ingredientIdToPumpMap = pumps.stream().collect(Collectors.toMap(x -> x.getCurrentIngredient().getId(), x-> x));
        Map<Integer, List<RecipeIngredient>> productionsStepMap = recipe.getRecipeIngredients().stream().collect(Collectors.groupingBy(x -> x.getId().getProductionStep()));
        List<Integer> steps = new ArrayList<>(productionsStepMap.keySet());
        steps.sort(Comparator.comparingInt(x -> x));
        for(Integer step : steps) {
            this.productionSteps.add(productionsStepMap.get(step));
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
        return getNeededIngredientIds().size() <= ingredientIdToPumpMap.size();
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

    private void calcPumpTimings() {
        if(!areEnoughPumpsAvailable()) {
            throw new NotEnoughPumpsException("Not enough pumps");
        }
        if(!doPumpsHaveAllIngredients()) {
            throw new BadIngredientAllocation("Bad ingredient allocation");
        }
        int currentOffset = 0;
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
                double pumpTime = ingPump.getTimePerClInMs() * (ingredient.getAmount() / 10d);
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

            for(Map.Entry<Pump, List<PumpPhase>> pumpPhases : pumpTimingStepCalculator.getPumpPhases().entrySet()) {
                if(recipePumpTimings.putIfAbsent(pumpPhases.getKey(), pumpPhases.getValue()) != null) {
                    recipePumpTimings.get(pumpPhases.getKey()).addAll(pumpPhases.getValue());
                    if(isLast) {
                        this.preparationTime = pumpPhases.getValue().stream().mapToInt(PumpPhase::getStopTime).max().getAsInt();
                    }
                }
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
                scheduledFutures.add(scheduler.scheduleWithFixedDelay(() -> {
                    System.out.println(pumpPhase.getPump().getGpioPin() + " started!");
                    this.notifyObservers();
                }, pumpPhase.getStartTime()));
                scheduledFutures.add(scheduler.scheduleWithFixedDelay(() -> {
                    System.out.println(pumpPhase.getPump().getGpioPin() + " stopped!");
                    this.notifyObservers();
                }, pumpPhase.getStopTime()));
            }
        }
        this.cocktailprogress = new Cocktailprogress();
        this.cocktailprogress.setUser(this.user);
        this.cocktailprogress.setRecipe(this.recipe);
        this.updateCocktailProgress();
        this.notifyObservers();
        return cocktailprogress;
    }

    public void cancelCocktail() {
        for(ScheduledFuture future : this.scheduledFutures) {
            future.cancel(true);
        }
        for(Pump pump : this.ingredientIdToPumpMap.values()) {
            //TODO turn off all pumps!
        }
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
        int progress = (int) (((double) runningSince) / this.preparationTime) * 100;
        progress = Math.min(progress, 100);
        this.cocktailprogress.setProgress(progress);
    }

    @Override
    public boolean addListener(Observer<Cocktailprogress> observer) {
        return this.observers.add(observer);
    }

    @Override
    public boolean removeListener(Observer<Cocktailprogress> observer) {
        return this.observers.remove(observer);
    }

    public void notifyObservers() {
        this.observers.forEach(x -> x.notify(this.cocktailprogress));
    }

    public Cocktailprogress getCocktailprogress() {
        return this.cocktailprogress;
    }
}
