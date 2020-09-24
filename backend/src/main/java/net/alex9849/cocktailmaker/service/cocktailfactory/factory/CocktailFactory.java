package net.alex9849.cocktailmaker.service.cocktailfactory.factory;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.model.exception.BadIngredientAllocation;
import net.alex9849.cocktailmaker.model.exception.NotEnoughPumpsException;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import net.alex9849.cocktailmaker.model.user.User;

import java.util.*;
import java.util.stream.Collectors;

public class CocktailFactory extends Thread {
    private Cocktailprogress cocktailprogress;
    private boolean running = false;
    private Collection<Pump> pumps;
    private List<List<RecipeIngredient>> productionStep = new ArrayList<>();

    public CocktailFactory(Recipe recipe, User user, Collection<Pump> pumps) {
        this.cocktailprogress = new Cocktailprogress();
        this.cocktailprogress.setRecipe(recipe);
        this.cocktailprogress.setUser(user);
        this.cocktailprogress.setProgress(0);
        this.cocktailprogress.setAborted(false);
        this.pumps = pumps;
        Map<Integer, List<RecipeIngredient>> productionsStepMap = recipe.getRecipeIngredients().stream().collect(Collectors.groupingBy(x -> x.getId().getProductionStep()));
        List<Integer> steps = new ArrayList<>(productionsStepMap.keySet());
        steps.sort(Comparator.comparingInt(x -> x));
        for(Integer step : steps) {
            this.productionStep.add(productionsStepMap.get(step));
        }
    }

    private List<Long> getNeededIngredientIds() {
        return cocktailprogress.getRecipe().getRecipeIngredients().stream().map(x -> x.getIngredient().getId()).collect(Collectors.toList());
    }

    private List<Long> getAvailableIngredientIds() {
        return pumps.stream().filter(x -> x.getCurrentIngredient() != null).map(x -> x.getCurrentIngredient().getId()).collect(Collectors.toList());
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

    @Override
    public synchronized void start() {
        if(!areEnoughPumpsAvailable()) {
            throw new NotEnoughPumpsException("Not enough pumps");
        }
        if(!doPumpsHaveAllIngredients()) {
            throw new BadIngredientAllocation("Bad ingredient allocation");
        }
        for(List<RecipeIngredient> productionStepIngredients : this.productionStep) {

        }
        super.start();
    }
}
