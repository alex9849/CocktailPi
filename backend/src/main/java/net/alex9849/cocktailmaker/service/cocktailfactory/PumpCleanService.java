package net.alex9849.cocktailmaker.service.cocktailfactory;

import com.pi4j.io.gpio.RaspiPin;
import net.alex9849.cocktailmaker.iface.IGpioController;
import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.service.PumpService;
import net.alex9849.cocktailmaker.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

@Service
public class PumpCleanService {
    private static PumpCleanService instance;

    @Autowired
    private CocktailFactoryService cocktailFactoryService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private PumpService pumpService;

    @Autowired
    private IGpioController gpioController;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Set<ScheduledFuture> scheduledFutures = new HashSet<>();
    private Set<Long> cleaningPumpIds = new ConcurrentSkipListSet<>();

    @PostConstruct
    public void init() {
        PumpCleanService.instance = this;
    }

    public static PumpCleanService getInstance() {
        return instance;
    }

    public void cleanPump(Pump pump) {
        if(isCleaning(pump)) {
            throw new IllegalArgumentException("Pump is already cleaning!");
        }
        int runTime = (int) (pump.getTimePerClInMs() / 10d) * pump.getTubeCapacityInMl();
        if (cocktailFactoryService.isMakingCocktail()) {
            throw new IllegalStateException("Can't clean pump! A cocktail is currently being made!");
        }
        this.cleaningPumpIds.add(pump.getId());
        gpioController.provideGpioPin(RaspiPin.getPinByAddress(pump.getGpioPin())).setHigh();
        System.out.println(pump.getGpioPin() + " started!");
        webSocketService.broadcastPumpLayout(pumpService.getAllPumps());
        scheduledFutures.add(scheduler.schedule(() -> {
            gpioController.provideGpioPin(RaspiPin.getPinByAddress(pump.getGpioPin())).setLow();
            System.out.println(pump.getGpioPin() + " stopped!");
            this.cleaningPumpIds.remove(pump.getId());
            webSocketService.broadcastPumpLayout(pumpService.getAllPumps());
        }, runTime, TimeUnit.MILLISECONDS));
    }

    public boolean isCleaning(Pump pump) {
        return cleaningPumpIds.contains(pump.getId());
    }

    public boolean isAnyCleaning() {
        return !cleaningPumpIds.isEmpty();
    }
}
