package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.iface.IGpioController;
import net.alex9849.cocktailmaker.model.FeasibilityReport;
import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.cocktail.CocktailProgress;
import net.alex9849.cocktailmaker.model.eventaction.EventTrigger;
import net.alex9849.cocktailmaker.model.recipe.FeasibilityFactory;
import net.alex9849.cocktailmaker.model.recipe.IngredientGroupReplacements;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.pump.PumpDto;
import net.alex9849.cocktailmaker.repository.PumpRepository;
import net.alex9849.cocktailmaker.service.cocktailfactory.CocktailFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class PumpService {

    private CocktailFactory cocktailFactory;

    @Autowired
    private PumpRepository pumpRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private IGpioController gpioController;

    @Autowired
    private EventService eventService;

    @Autowired
    private IngredientService ingredientService;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Set<Long> cleaningPumpIds = new ConcurrentSkipListSet<>();

    public void turnAllPumpsOff() {
        List<Pump> pumps = getAllPumps();
        //Turn off all pumps
        pumps.forEach(pump -> {
            gpioController.getGpioPin(pump.getBcmPin()).setHigh();
        });
    }

    public List<Pump> getAllPumps() {
        return pumpRepository.findAll();
    }

    public Pump getPump(long id) {
        return pumpRepository.findById(id).orElse(null);
    }

    public Pump createPump(Pump pump) {
        if(pumpRepository.findByBcmPin(pump.getBcmPin()).isPresent()) {
            throw new IllegalArgumentException("GPOI-Pin already in use!");
        }
        pump = pumpRepository.create(pump);
        //Turn off pump
        gpioController.getGpioPin(pump.getBcmPin()).setHigh();
        webSocketService.broadcastPumpLayout(getAllPumps());
        return pump;
    }

    public Pump updatePump(Pump pump) {
        Optional<Pump> beforeUpdate = pumpRepository.findById(pump.getId());
        if(!beforeUpdate.isPresent()) {
            throw new IllegalArgumentException("Pump doesn't exist!");
        }
        Optional<Pump> optPumpWithGpio = pumpRepository.findByBcmPin(pump.getBcmPin());
        if(optPumpWithGpio.isPresent()) {
            if(optPumpWithGpio.get().getId() != pump.getId()) {
                throw new IllegalArgumentException("GPOI-Pin already in use!");
            }
        }
        pumpRepository.update(pump);
        if(beforeUpdate.get().getBcmPin() != pump.getBcmPin()) {
            gpioController.getGpioPin(beforeUpdate.get().getBcmPin()).setHigh();
            gpioController.getGpioPin(pump.getBcmPin()).setHigh();
        }
        webSocketService.broadcastPumpLayout(getAllPumps());
        return pump;
    }

    public Pump fromDto(PumpDto.Request.Create pumpDto) {
        if(pumpDto == null) {
            return null;
        }
        Pump pump = new Pump();
        BeanUtils.copyProperties(pumpDto, pump);
        if(pumpDto.getCurrentIngredientId() != null) {
            Ingredient ingredient = ingredientService.getIngredient(pumpDto.getCurrentIngredientId());
            if(ingredient == null) {
                throw new IllegalArgumentException("Ingredient with id \"" + pump.getCurrentIngredientId() + "\" not found!");
            }
            if(!(ingredient instanceof AutomatedIngredient)) {
                throw new IllegalArgumentException("Ingredient must be an AutomatedIngredient!");
            }
            pump.setCurrentIngredient((AutomatedIngredient) ingredient);
        }
        return pump;
    }

    public void deletePump(long id) {
        Pump pump = getPump(id);
        if(pump == null) {
            throw new IllegalArgumentException("Pump doesn't exist!");
        }
        pumpRepository.delete(id);
        //Turn off pump
        gpioController.getGpioPin(pump.getBcmPin()).setHigh();
        webSocketService.broadcastPumpLayout(getAllPumps());
    }

    public synchronized void orderCocktail(User user, Recipe recipe, int amount) {
        if(this.isMakingCocktail()) {
            throw new IllegalArgumentException("A cocktail is already being fabricated!");
        }
        if(isAnyCleaning()) {
            throw new IllegalStateException("There are pumps getting cleaned currently!");
        }
        CocktailFactory.transformToAmountOfLiquid(recipe, amount);
        FeasibilityReport report = this.checkFeasibility(recipe);
        if(!report.getInsufficientIngredients().isEmpty()) {
            throw new IllegalArgumentException("Some pumps don't have enough liquids left!");
        }
        //Toto Replacement source
        FeasibilityFactory feasibilityFactory = new FeasibilityFactory(recipe, new IngredientGroupReplacements(), getAllPumps());
        if(!feasibilityFactory.getFeasibilityReport().isFeasible()) {
            throw new IllegalArgumentException("Cocktail not feasible!");
        }
        this.cocktailFactory = new CocktailFactory(feasibilityFactory.getFeasibleRecipe(), user, new HashSet<>(getAllPumps()), gpioController)
                .subscribeProgress(this::onCocktailProgressSubscriptionChange);
        for(Pump pump : this.cocktailFactory.getUsedPumps()) {
            this.pumpRepository.update(pump);
        }
        webSocketService.broadcastPumpLayout(getAllPumps());
        this.cocktailFactory.makeCocktail();
    }

    private void onCocktailProgressSubscriptionChange(CocktailProgress progress) {
        if(progress.getState() == CocktailProgress.State.CANCELLED || progress.getState() == CocktailProgress.State.FINISHED) {
            this.scheduler.schedule(() -> {
                this.cocktailFactory = null;
                this.webSocketService.broadcastCurrentCocktailProgress(null);
            }, 5000, TimeUnit.MILLISECONDS);
        }
        this.webSocketService.broadcastCurrentCocktailProgress(progress);

        switch (progress.getState()) {
            case RUNNING:
                if (progress.getPreviousState() == CocktailProgress.State.READY_TO_START) {
                    eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_STARTED);
                }
                break;
            case MANUAL_ACTION_REQUIRED:
            case MANUAL_INGREDIENT_ADD:
                eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_MANUAL_INTERACTION_REQUESTED);
                break;
            case CANCELLED:
                eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_CANCELED);
                break;
            case FINISHED:
                eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_FINISHED);
                break;
        }
    }

    public FeasibilityReport checkFeasibility(Recipe recipe, int amount) {
        return checkFeasibility(CocktailFactory.transformToAmountOfLiquid(recipe, amount));
    }

    public FeasibilityReport checkFeasibility(Recipe recipe) {
        //Todo Replacement source
        FeasibilityFactory feasibilityFactory = new FeasibilityFactory(recipe, new IngredientGroupReplacements(), this.getAllPumps());
        return feasibilityFactory.getFeasibilityReport();
    }

    public synchronized void continueCocktailProduction() {
        if (this.cocktailFactory == null || this.cocktailFactory.isFinished()) {
            throw new IllegalStateException("No cocktail is being prepared currently!");
        }
        this.cocktailFactory.continueProduction();
    }

    public synchronized boolean isMakingCocktail() {
        return this.cocktailFactory != null;
    }

    public synchronized boolean cancelCocktailOrder() {
        if(this.cocktailFactory == null || this.cocktailFactory.isFinished()) {
            return false;
        }
        this.cocktailFactory.cancelCocktail();
        return true;
    }

    public CocktailProgress getCurrentCocktailProgress() {
        if(this.cocktailFactory == null) {
            return null;
        }
        return this.cocktailFactory.getCocktailprogress();
    }

    public synchronized void cleanPump(Pump pump) {
        if(isCleaning(pump)) {
            throw new IllegalArgumentException("Pump is already cleaning!");
        }
        double multiplier = 1.0;
        if(pump.getCurrentIngredient() != null) {
            multiplier = ((AutomatedIngredient) pump.getCurrentIngredient()).getPumpTimeMultiplier();
        }
        int runTime = (int) (pump.getTimePerClInMs() * multiplier / 10d) * pump.getTubeCapacityInMl();
        if (this.isMakingCocktail()) {
            throw new IllegalStateException("Can't clean pump! A cocktail is currently being made!");
        }
        this.cleaningPumpIds.add(pump.getId());
        gpioController.getGpioPin(pump.getBcmPin()).setLow();
        webSocketService.broadcastPumpLayout(getAllPumps());
        scheduler.schedule(() -> {
            gpioController.getGpioPin(pump.getBcmPin()).setHigh();
            this.cleaningPumpIds.remove(pump.getId());
            webSocketService.broadcastPumpLayout(getAllPumps());
        }, runTime, TimeUnit.MILLISECONDS);
    }

    public boolean isCleaning(Pump pump) {
        return cleaningPumpIds.contains(pump.getId());
    }

    public boolean isAnyCleaning() {
        return !cleaningPumpIds.isEmpty();
    }
}
