package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.cocktail.CocktailProgress;
import net.alex9849.cocktailmaker.model.pump.JobMetrics;
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
import net.alex9849.cocktailmaker.model.pump.PumpAdvice;
import net.alex9849.cocktailmaker.service.pumps.PumpMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PumpService {

    @Autowired
    private PumpMaintenanceService maintenanceService;

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
        maintenanceService.stopAllPumps();
        try {
            for (Pump pump : dataService.getAllPumps()) {
                if (!pump.isCanPumpUp()) {
                    continue;
                }
                lockService.acquirePumpLock(pump.getId(), maintenanceService);
                maintenanceService.dispatchPumpJob(pump, new PumpAdvice(PumpAdvice.Type.RUN, 0), () -> {
                    lockService.releasePumpLock(pump.getId(), maintenanceService);
                    maintenanceService.reschedulePumpBack();
                });
            }
        } finally {
            lockService.releaseGlobal(maintenanceService);
        }
        maintenanceService.reschedulePumpBack();
    }

    public void stopAllPumps() {
        maintenanceService.stopAllPumps();
    }

    public void cancelPumpUp(long pumpId) {
        maintenanceService.cancelByPumpId(pumpId);
    }

    public long performPumpAdvice(Pump pump, PumpAdvice advice) {
        if (!lockService.testAndAcquirePumpLock(pump.getId(), maintenanceService)) {
            throw new IllegalArgumentException("Pumps are currently occupied!");
        }
        long jobId = maintenanceService.dispatchPumpJob(pump, advice,
                () -> {
                    try {
                        maintenanceService.reschedulePumpBack();
                        if(advice.getType() == PumpAdvice.Type.PUMP_UP || advice.getType() == PumpAdvice.Type.PUMP_DOWN) {
                            dataService.updatePump(pump);
                            webSocketService.broadcastPumpLayout(dataService.getAllPumps());
                        }
                    } finally {
                        lockService.releasePumpLock(pump.getId(), maintenanceService);
                    }
                });
        maintenanceService.reschedulePumpBack();
        return jobId;
    }

    public JobMetrics getJobMetrics(long id) {
        return maintenanceService.getJobMetrics(id);
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
    public Pump fromDto(PumpDto.Request.Create pumpDto) {
        return dataService.fromDto(pumpDto);
    }

    public Pump fromDto(PumpDto.Request.Create patchPumpDto, Pump toPatch) {
        return dataService.fromDto(patchPumpDto, toPatch);
    }

    public CocktailOrderConfiguration fromDto(CocktailOrderConfigurationDto.Request.Create orderConfigDto) {
        return cocktailOrderService.fromDto(orderConfigDto);
    }

}
