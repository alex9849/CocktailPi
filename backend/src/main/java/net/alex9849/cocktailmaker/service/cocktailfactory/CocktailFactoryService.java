package net.alex9849.cocktailmaker.service.cocktailfactory;

import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.service.PumpService;
import net.alex9849.cocktailmaker.service.WebSocketService;
import net.alex9849.cocktailmaker.service.cocktailfactory.factory.CocktailFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class CocktailFactoryService implements Observer {

    private CocktailFactory cocktailFactory;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private PumpService pumpService;

    @Autowired
    private PumpCleanService pumpCleanService;

    public synchronized Cocktailprogress orderCocktail(User user, Recipe recipe, int amount) {
        if(this.isMakingCocktail()) {
            throw new IllegalArgumentException("A cocktail is already being prepared!");
        }
        if(pumpCleanService.isAnyCleaning()) {
            throw new IllegalStateException("There are pumps getting cleaned currently!");
        }
        this.cocktailFactory = new CocktailFactory(recipe, user, pumpService.getAllPumps(), amount);
        this.cocktailFactory.addObserver(this);
        return this.cocktailFactory.makeCocktail();
    }

    public synchronized boolean isMakingCocktail() {
        return this.cocktailFactory != null;
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
    public void update(Observable o, Object arg) {
        if(arg instanceof Cocktailprogress) {
            Cocktailprogress progress = (Cocktailprogress) arg;
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
}
