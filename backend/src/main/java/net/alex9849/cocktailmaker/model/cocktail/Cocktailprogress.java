package net.alex9849.cocktailmaker.model.cocktail;

import net.alex9849.cocktailmaker.model.recipe.AddIngredient;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.user.User;

import java.util.List;

public class Cocktailprogress {
    private Recipe recipe;
    private int progress;
    private User user;
    private State state;
    private List<AddIngredient> currentIngredientsToAddManually;


    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public List<AddIngredient> getCurrentIngredientsToAddManually() {
        return currentIngredientsToAddManually;
    }

    public void setCurrentIngredientsToAddManually(List<AddIngredient> ingredients) {
        this.currentIngredientsToAddManually = ingredients;
    }

    public enum State {
        RUNNING, MANUAL_ACTION_REQUIRED, CANCELLED, FINISHED, READY_TO_START
    }
}
