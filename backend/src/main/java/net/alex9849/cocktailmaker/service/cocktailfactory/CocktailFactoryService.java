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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class CocktailFactoryService implements Observer<Cocktailprogress> {

    private CocktailFactory cocktailFactory;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private PumpService pumpService;

    public synchronized Cocktailprogress orderCocktail(User user, Recipe recipe, int amount) {
        if(this.cocktailFactory != null) {
            throw new IllegalArgumentException("A cocktail is already being prepared!");
        }
        this.cocktailFactory = new CocktailFactory(recipe, user, pumpService.getAllPumps(), amount);
        this.cocktailFactory.addListener(this);
        return this.cocktailFactory.makeCocktail();
    }

    public synchronized boolean cancelOrder() {
        if(this.cocktailFactory == null || this.cocktailFactory.isDone() || !this.cocktailFactory.isRunning()) {
            return false;
        }
        this.cocktailFactory.cancelCocktail();
        return true;
    }

    public Cocktailprogress getCurrentProgress() {
        if(this.cocktailFactory == null) {
            return null;
        }
        return this.cocktailFactory.getCocktailprogress();
    }

    @Override
    public void notify(Cocktailprogress progress) {
        if(progress.isCanceled() || progress.isDone()) {
            this.scheduler.schedule(() -> {
                this.cocktailFactory.shutDown();
                this.cocktailFactory = null;
                this.webSocketService.broadcastCurrentCocktail(null);
            }, 5000, TimeUnit.MILLISECONDS);
        }
        this.webSocketService.broadcastCurrentCocktail(progress);
    }
}
