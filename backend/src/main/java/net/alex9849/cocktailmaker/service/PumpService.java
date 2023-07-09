package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.cocktail.CocktailProgress;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.model.recipe.CocktailOrderConfiguration;
import net.alex9849.cocktailmaker.model.recipe.FeasibilityFactory;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.cocktail.CocktailOrderConfigurationDto;
import net.alex9849.cocktailmaker.payload.dto.pump.PumpDto;
import net.alex9849.cocktailmaker.payload.dto.settings.ReversePumpingSettings;
import net.alex9849.cocktailmaker.service.pumps.CocktailOrderService;
import net.alex9849.cocktailmaker.service.pumps.PumpDataService;
import net.alex9849.cocktailmaker.service.pumps.PumpLockService;
import net.alex9849.cocktailmaker.service.pumps.PumpUpService;
import net.alex9849.motorlib.Direction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PumpService {

    @Autowired
    private PumpUpService maintenanceService;

    @Autowired
    private PumpLockService lockService;

    @Autowired
    private PumpDataService dataService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private CocktailOrderService cocktailOrderService;

    public void postConstruct() {
        maintenanceService.stopAllPumps();
    }

    public List<Pump> getAllPumps() {
        return dataService.getAllPumps();
    }

    public Pump getPump(long id) {
        return dataService.getPump(id);
    }

    public Pump createPump(Pump pump) {
        Pump newPump = dataService.createPump(pump);
        webSocketService.broadcastPumpLayout(dataService.getAllPumps());
        return newPump;
    }

    public Pump updatePump(Pump pump) {
        if (!lockService.testAndAcquirePumpLock(pump.getId(), dataService)) {
            throw new IllegalArgumentException("Pumps are currently occupied!");
        }
        try {
            Pump updatedPump = dataService.updatePump(pump);
            webSocketService.broadcastPumpLayout(getAllPumps());
            return updatedPump;
        } finally {
            lockService.releasePumpLock(pump.getId(), dataService);
        }
    }

    public void deletePump(long id) {
        if (!lockService.testAndAcquirePumpLock(id, dataService)) {
            throw new IllegalArgumentException("Pumps are currently occupied!");
        }
        try {
            dataService.deletePump(id);
            webSocketService.broadcastPumpLayout(getAllPumps());
        } finally {
            lockService.releasePumpLock(id, dataService);
        }
    }

    public void runAllPumps() {
        if (!lockService.testAndAcquireGlobal(maintenanceService)) {
            throw new IllegalArgumentException("Some pumps are currently occupied!");
        }
        try {
            for (Pump pump : dataService.getAllPumps()) {
                if (!pump.isCanPump()) {
                    continue;
                }
                runPumpOrPerformPumpUp(pump, Direction.FORWARD, false);
                lockService.acquirePumpLock(pump.getId(), maintenanceService);
                maintenanceService.runPumpOrPerformPumpUp(pump, Direction.FORWARD, false, () -> {
                    try {
                        dataService.updatePump(pump);
                        webSocketService.broadcastPumpLayout(dataService.getAllPumps());
                    } finally {
                        lockService.releasePumpLock(pump.getId(), maintenanceService);
                    }
                });
            }
            maintenanceService.reschedulePumpBack();
            webSocketService.broadcastPumpLayout(dataService.getAllPumps());
        } finally {
            lockService.releaseGlobal(maintenanceService);
        }
    }

    public void stopAllPumps() {
        maintenanceService.stopAllPumps();
        maintenanceService.reschedulePumpBack();
        webSocketService.broadcastPumpLayout(dataService.getAllPumps());
    }

    public void cancelPumpUp(long pumpId) {
        maintenanceService.cancelPumpUp(pumpId);
        maintenanceService.reschedulePumpBack();
        webSocketService.broadcastPumpLayout(dataService.getAllPumps());
    }

    public void runPumpOrPerformPumpUp(Pump pump, Direction direction, boolean isPumpUp) {
        if (!lockService.testAndAcquirePumpLock(pump.getId(), maintenanceService)) {
            throw new IllegalArgumentException("Pumps are currently occupied!");
        }
        maintenanceService.runPumpOrPerformPumpUp(pump, direction, isPumpUp,
                () -> {
                    try {
                        dataService.updatePump(pump);
                        webSocketService.broadcastPumpLayout(dataService.getAllPumps());
                    } finally {
                        lockService.releasePumpLock(pump.getId(), maintenanceService);
                    }
                });
        maintenanceService.reschedulePumpBack();
        webSocketService.broadcastPumpLayout(dataService.getAllPumps());
    }

    public void setReversePumpingSettings(ReversePumpingSettings.Full settings) {
        if (!lockService.testAndAcquireGlobal(maintenanceService)) {
            throw new IllegalArgumentException("Some pumps are currently occupied!");
        }
        try {
            maintenanceService.setReversePumpingSettings(settings);
        } finally {
            lockService.releaseGlobal(maintenanceService);
        }
    }

    public void orderCocktail(User user, Recipe recipe, CocktailOrderConfiguration orderConfiguration) {
        if (!lockService.testAndAcquireGlobal(maintenanceService)) {
            throw new IllegalArgumentException("Some pumps are currently occupied!");
        }
        try {
            cocktailOrderService.orderCocktail(user, recipe, orderConfiguration);
        } finally {
            lockService.releaseGlobal(maintenanceService);
        }
    }

    public FeasibilityFactory checkFeasibility(Recipe recipe, CocktailOrderConfiguration orderConfig) {
        return cocktailOrderService.checkFeasibility(recipe, orderConfig);
    }

    public void continueCocktailProduction() {
        cocktailOrderService.continueCocktailProduction();
    }

    public boolean cancelCocktailOrder() {
        return cocktailOrderService.cancelCocktailOrder();
    }

    public CocktailProgress getCurrentCocktailProgress() {
        return cocktailOrderService.getCurrentCocktailProgress();
    }
    public Pump fromDto(PumpDto.Request.Patch pumpDto) {
        return dataService.fromDto(pumpDto);
    }

    public Pump fromDto(PumpDto.Request.Patch patchPumpDto, Pump toPatch) {
        return dataService.fromDto(patchPumpDto, toPatch);
    }

    public CocktailOrderConfiguration fromDto(CocktailOrderConfigurationDto.Request.Create orderConfigDto) {
        return cocktailOrderService.fromDto(orderConfigDto);
    }

    public PumpOccupation getPumpOccupation(long pumpId) {
        if(this.cocktailOrderService.isMakingCocktail()) {
            return PumpOccupation.COCKTAIL_PRODUCTION;
        }
        if(this.maintenanceService.isPumpPumpingUp(pumpId)) {
            return PumpOccupation.PUMPING_UP;
        }
        if(this.maintenanceService.isPumpRunning(pumpId)) {
            return PumpOccupation.RUNNING;
        }
        return PumpOccupation.NONE;
    }

    public enum PumpOccupation {
        RUNNING, PUMPING_UP, COCKTAIL_PRODUCTION, NONE
    }

}
