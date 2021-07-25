package net.alex9849.cocktailmaker.model.cocktail;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import net.alex9849.cocktailmaker.model.user.User;

public class Cocktailprogress {
    private Recipe recipe;
    private int progress;
    private User user;
    private State state;
    private RecipeIngredient currentRequiredAction;


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

    public RecipeIngredient getCurrentRequiredAction() {
        return currentRequiredAction;
    }

    public void setCurrentRequiredAction(RecipeIngredient currentRequiredAction) {
        this.currentRequiredAction = currentRequiredAction;
    }

    public enum State {
        RUNNING, MANUAL_ACTION_REQUIRED, CANCELLED, COMPLETE
    }
}
