package net.alex9849.cocktailpi.model.cocktail;

import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.model.user.User;

import java.util.List;

public class CocktailProgress {
    private Recipe recipe;
    private int progress;
    private User user;
    private State previousState;
    private State state;
    private List<ProductionStepIngredient> currentIngredientsToAddManually;
    private String writtenInstruction;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public String getWrittenInstruction() {
        return writtenInstruction;
    }

    public void setWrittenInstruction(String writtenInstruction) {
        this.writtenInstruction = writtenInstruction;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public State getPreviousState() {
        return previousState;
    }

    public void setPreviousState(State previousState) {
        this.previousState = previousState;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public List<ProductionStepIngredient> getCurrentIngredientsToAddManually() {
        return currentIngredientsToAddManually;
    }

    public void setCurrentIngredientsToAddManually(List<ProductionStepIngredient> ingredients) {
        this.currentIngredientsToAddManually = ingredients;
    }

    public void setCurrentWrittenInstruction(String message) {
        this.writtenInstruction = message;
    }

    public enum State {
        RUNNING, MANUAL_INGREDIENT_ADD, MANUAL_ACTION_REQUIRED, CANCELLED, FINISHED, ERROR, READY_TO_START
    }
}
