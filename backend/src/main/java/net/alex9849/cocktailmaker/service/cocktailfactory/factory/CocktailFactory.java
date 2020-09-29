package net.alex9849.cocktailmaker.service.cocktailfactory.factory;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.model.exception.BadIngredientAllocation;
import net.alex9849.cocktailmaker.model.exception.NotEnoughPumpsException;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import net.alex9849.cocktailmaker.model.user.User;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CocktailFactory {
    private Cocktailprogress cocktailprogress;
    private Map<Long, Pump> ingredientIdToPumpMap;
    private List<List<RecipeIngredient>> productionSteps = new ArrayList<>();
    private final int MINIMAL_PUMP_OPERATION_TIME_IN_MS = 500;
    private final int MINIMAL_PUMP_BREAK_TIME_IN_MS = 500;
    private ScheduledExecutorService pumpExecutor = Executors.newSingleThreadScheduledExecutor();
    private Set<ScheduledFuture> scheduledFutures = new HashSet<>();

    public CocktailFactory(Recipe recipe, User user, Collection<Pump> pumps) {
        this.cocktailprogress = new Cocktailprogress();
        this.cocktailprogress.setRecipe(recipe);
        this.cocktailprogress.setUser(user);
        this.cocktailprogress.setProgress(0);
        this.cocktailprogress.setAborted(false);
        this.ingredientIdToPumpMap = pumps.stream().collect(Collectors.toMap(x -> x.getCurrentIngredient().getId(), x-> x));
        Map<Integer, List<RecipeIngredient>> productionsStepMap = recipe.getRecipeIngredients().stream().collect(Collectors.groupingBy(x -> x.getId().getProductionStep()));
        List<Integer> steps = new ArrayList<>(productionsStepMap.keySet());
        steps.sort(Comparator.comparingInt(x -> x));
        for(Integer step : steps) {
            this.productionSteps.add(productionsStepMap.get(step));
        }
    }

    private List<Long> getNeededIngredientIds() {
        return cocktailprogress.getRecipe().getRecipeIngredients().stream().map(x -> x.getIngredient().getId()).collect(Collectors.toList());
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

    private Map<Pump, List<PumpPhase>> getPumpTimings() {
        if(!areEnoughPumpsAvailable()) {
            throw new NotEnoughPumpsException("Not enough pumps");
        }
        if(!doPumpsHaveAllIngredients()) {
            throw new BadIngredientAllocation("Bad ingredient allocation");
        }
        int currentOffset = 0;
        Map<Pump, List<PumpPhase>> recipePumpTimings = new HashMap<>();

        for(List<RecipeIngredient> productionStep : this.productionSteps) {
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
                }
            }
        }
        return recipePumpTimings;
    }

    public void makeCocktail() {
        if(this.isRunning()) {
            throw new IllegalArgumentException("Already running!");
        }
        Map<Pump, List<PumpPhase>> pumpPhases = this.getPumpTimings();
        pumpExecutor = Executors.newSingleThreadScheduledExecutor();
        for(Map.Entry<Pump, List<PumpPhase>> pumpPumpPhases : pumpPhases.entrySet()) {
            for(PumpPhase pumpPhase : pumpPumpPhases.getValue()) {
                scheduledFutures.add(pumpExecutor.schedule(() -> {
                    System.out.println(pumpPhase.getPump().getGpioPin() + " started!");
                }, pumpPhase.getStartTime(), TimeUnit.MILLISECONDS));
                scheduledFutures.add(pumpExecutor.schedule(() -> {
                    System.out.println(pumpPhase.getPump().getGpioPin() + " stopped!");
                }, pumpPhase.getStopTime(), TimeUnit.MILLISECONDS));
            }
        }
    }

    public boolean isRunning() {
        if(this.scheduledFutures.stream().anyMatch(x -> !x.isDone())) {
            return true;
        }
        this.scheduledFutures = new HashSet<>();
        return false;
    }
}
