package net.alex9849.cocktailmaker.service;

import com.pi4j.io.gpio.RaspiPin;
import net.alex9849.cocktailmaker.iface.IGpioController;
import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.pump.PumpDto;
import net.alex9849.cocktailmaker.repository.PumpRepository;
import net.alex9849.cocktailmaker.service.cocktailfactory.CocktailFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class PumpService implements Observer {

    private static PumpService instance;

    private CocktailFactory cocktailFactory;

    @Autowired
    private PumpRepository pumpRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IGpioController gpioController;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Set<Long> cleaningPumpIds = new ConcurrentSkipListSet<>();

    @PostConstruct
    public void init() {
        PumpService.instance = this;
        List<Pump> pumps = getAllPumps();
        //Turn off all pumps
        pumps.forEach(pump -> {
            gpioController.provideGpioPin(RaspiPin.getPinByAddress(pump.getGpioPin())).setHigh();
        });
    }

    public static PumpService getInstance() {
        return instance;
    }

    public List<Pump> getAllPumps() {
        return pumpRepository.findAll();
    }

    public Pump getPump(long id) {
        return pumpRepository.findById(id).orElse(null);
    }

    public Pump createPump(Pump pump) {
        if(pumpRepository.findByGpioPin(pump.getGpioPin()).isPresent()) {
            throw new IllegalArgumentException("GPOI-Pin already in use!");
        }
        pump = pumpRepository.create(pump);
        //Turn off pump
        gpioController.provideGpioPin(RaspiPin.getPinByAddress(pump.getGpioPin())).setHigh();
        webSocketService.broadcastPumpLayout(getAllPumps());
        return pump;
    }

    public Pump updatePump(Pump pump) {
        Optional<Pump> beforeUpdate = pumpRepository.findById(pump.getId());
        if(!beforeUpdate.isPresent()) {
            throw new IllegalArgumentException("Pump doesn't exist!");
        }
        Optional<Pump> optPumpWithGpio = pumpRepository.findByGpioPin(pump.getGpioPin());
        if(optPumpWithGpio.isPresent()) {
            if(optPumpWithGpio.get().getId() != pump.getId()) {
                throw new IllegalArgumentException("GPOI-Pin already in use!");
            }
        }
        pumpRepository.update(pump);
        if(optPumpWithGpio.isPresent()) {
            gpioController.provideGpioPin(RaspiPin.getPinByAddress(beforeUpdate.get().getGpioPin())).setLow();
            gpioController.provideGpioPin(RaspiPin.getPinByAddress(pump.getGpioPin())).setHigh();
        }
        webSocketService.broadcastPumpLayout(getAllPumps());
        return pump;
    }

    public Pump fromDto(PumpDto pumpDto) {
        if(pumpDto == null) {
            return null;
        }
        Pump pump = new Pump();
        BeanUtils.copyProperties(pumpDto, pump);
        pump.setCurrentIngredient(ingredientService.fromDto(pumpDto.getCurrentIngredient()));
        return pump;
    }

    public void deletePump(long id) {
        Pump pump = getPump(id);
        if(pump == null) {
            throw new IllegalArgumentException("Pump doesn't exist!");
        }
        pumpRepository.delete(id);
        //Turn off pump
        gpioController.provideGpioPin(RaspiPin.getPinByAddress(pump.getGpioPin())).setHigh();
        webSocketService.broadcastPumpLayout(getAllPumps());
    }

    public synchronized Cocktailprogress orderCocktail(User user, Recipe recipe, int amount) {
        if(this.isMakingCocktail()) {
            throw new IllegalArgumentException("A cocktail is already being fabricated!");
        }
        if(isAnyCleaning()) {
            throw new IllegalStateException("There are pumps getting cleaned currently!");
        }
        this.cocktailFactory = new CocktailFactory(recipe, user, getAllPumps(), gpioController, amount);
        this.cocktailFactory.addObserver(this);
        return this.cocktailFactory.makeCocktail();
    }

    public synchronized boolean isMakingCocktail() {
        return this.cocktailFactory != null;
    }

    public synchronized boolean cancelCocktailOrder() {
        if(this.cocktailFactory == null || this.cocktailFactory.isDone()) {
            return false;
        }
        this.cocktailFactory.cancelCocktail();
        return true;
    }

    public Cocktailprogress getCurrentCocktailProgress() {
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
        gpioController.provideGpioPin(RaspiPin.getPinByAddress(pump.getGpioPin())).setLow();
        webSocketService.broadcastPumpLayout(getAllPumps());
        scheduler.schedule(() -> {
            gpioController.provideGpioPin(RaspiPin.getPinByAddress(pump.getGpioPin())).setHigh();
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
