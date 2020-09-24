package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CocktailFactoryService {

    private Cocktailprogress currentProgress = null;

    @Autowired
    private WebSocketService webSocketService;

    public synchronized Cocktailprogress orderCocktail(User user, Recipe recipe) {
        if(this.currentProgress != null) {
            throw new IllegalArgumentException("A cocktail is already being prepared!");
        }
        Cocktailprogress cocktailprogress = new Cocktailprogress();
        cocktailprogress.setAborted(false);
        cocktailprogress.setProgress(0);
        cocktailprogress.setUser(user);
        cocktailprogress.setRecipe(recipe);
        this.currentProgress = cocktailprogress;
        this.webSocketService.broadcastCurrentCocktail(this.currentProgress);
        return cocktailprogress;
    }

    public synchronized boolean cancelOrder() {
        if(this.currentProgress == null) {
            return false;
        }
        this.currentProgress = null;
        this.webSocketService.broadcastCurrentCocktail(this.currentProgress);
        return true;
    }

    public Cocktailprogress getCurrentProgress() {
        return currentProgress;
    }
}
