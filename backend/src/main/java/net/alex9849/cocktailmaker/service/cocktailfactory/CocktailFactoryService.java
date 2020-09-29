package net.alex9849.cocktailmaker.service.cocktailfactory;

import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.model.util.Observer;
import net.alex9849.cocktailmaker.service.PumpService;
import net.alex9849.cocktailmaker.service.WebSocketService;
import net.alex9849.cocktailmaker.service.cocktailfactory.factory.CocktailFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CocktailFactoryService implements Observer<Cocktailprogress> {

    private CocktailFactory cocktailFactory;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private PumpService pumpService;

    public synchronized Cocktailprogress orderCocktail(User user, Recipe recipe) {
        if(this.cocktailFactory != null && this.cocktailFactory.isRunning()) {
            throw new IllegalArgumentException("A cocktail is already being prepared!");
        }
        if(this.cocktailFactory != null) {
            this.cocktailFactory.cancelCocktail();

        }
        this.cocktailFactory = new CocktailFactory(recipe, user, pumpService.getAllPumps());
        this.cocktailFactory.addListener(this);
        return this.cocktailFactory.makeCocktail();
    }

    public synchronized boolean cancelOrder() {
        if(this.cocktailFactory == null || this.cocktailFactory.isDone() || !this.cocktailFactory.isRunning()) {
            return false;
        }
        this.cocktailFactory.cancelCocktail();
        this.webSocketService.broadcastCurrentCocktail(null);
        return true;
    }

    public Cocktailprogress getCurrentProgress() {
        if(this.cocktailFactory == null) {
            return null;
        }
        return this.cocktailFactory.getCocktailprogress();
    }

    @Override
    public void notify(Cocktailprogress object) {
        this.webSocketService.broadcastCurrentCocktail(object);
    }
}
