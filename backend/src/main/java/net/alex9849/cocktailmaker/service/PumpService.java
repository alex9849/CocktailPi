package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.iface.IGpioController;
import net.alex9849.cocktailmaker.iface.IGpioPin;
import net.alex9849.cocktailmaker.model.FeasibilityReport;
import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.cocktail.CocktailProgress;
import net.alex9849.cocktailmaker.model.eventaction.EventTrigger;
import net.alex9849.cocktailmaker.model.recipe.FeasibilityFactory;
import net.alex9849.cocktailmaker.model.recipe.CocktailOrderConfiguration;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.ingredient.IngredientGroup;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.cocktail.CocktailOrderConfigurationDto;
import net.alex9849.cocktailmaker.payload.dto.cocktail.FeasibilityReportDto;
import net.alex9849.cocktailmaker.payload.dto.pump.PumpDto;
import net.alex9849.cocktailmaker.payload.dto.settings.ReversePumpingSettings;
import net.alex9849.cocktailmaker.repository.OptionsRepository;
import net.alex9849.cocktailmaker.repository.PumpRepository;
import net.alex9849.cocktailmaker.service.cocktailfactory.CocktailFactory;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

@Service
@Transactional
public class PumpService {
    @Autowired
    private PumpRepository pumpRepository;

    @Autowired
    private CocktailOrderService cocktailOrderService;

    @Autowired
    private PumpUpService pumpUpService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private IngredientService ingredientService;

    public void postConstruct() {
        this.turnOnOffPumps(false);
    }

    //
    // CRUD actions
    //
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
        pump.setRunning(false);
        webSocketService.broadcastPumpLayout(getAllPumps());
        return pump;
    }

    public Set<Pump> updatePumps(Set<Pump> pumps) {
        Set<Pump> updated = new HashSet<>();
        for(Pump pump : pumps) {
            updated.add(this.internalUpdatePump(pump));
        }
        webSocketService.broadcastPumpLayout(getAllPumps());
        return updated;
    }

    public Pump updatePump(Pump pump) {
        Pump updated = this.internalUpdatePump(pump);
        webSocketService.broadcastPumpLayout(getAllPumps());
        return updated;
    }

    private Pump internalUpdatePump(Pump pump) {
        Optional<Pump> beforeUpdate = pumpRepository.findById(pump.getId());
        if(!beforeUpdate.isPresent()) {
            throw new IllegalArgumentException("Pump doesn't exist!");
        }
        if(getPumpOccupation(beforeUpdate.get()) != PumpOccupation.NONE) {
            throw new IllegalStateException("Pump currently occupied!");
        }
        Optional<Pump> optPumpWithGpio = pumpRepository.findByBcmPin(pump.getBcmPin());
        if(optPumpWithGpio.isPresent()) {
            if(optPumpWithGpio.get().getId() != pump.getId()) {
                throw new IllegalArgumentException("GPOI-Pin already in use!");
            }
        }
        pumpRepository.update(pump);
        if(beforeUpdate.get().getBcmPin() != pump.getBcmPin()) {
            beforeUpdate.get().setRunning(false);
            pump.setRunning(false);
        }
        if(beforeUpdate.get().isPowerStateHigh() != pump.isPowerStateHigh()) {
            pump.setRunning(false);
        }
        return pump;
    }

    public Optional<Pump> findByBcmPin(int bcmPin) {
        return pumpRepository.findByBcmPin(bcmPin);
    }

    public void deletePump(long id) {
        Pump pump = getPump(id);
        if(pump == null) {
            throw new IllegalArgumentException("Pump doesn't exist!");
        }
        if(getPumpOccupation(pump) != PumpOccupation.NONE) {
            throw new IllegalStateException("Pump currently occupied!");
        }
        pumpRepository.delete(id);
        //Turn off pump
        pump.setRunning(false);
        webSocketService.broadcastPumpLayout(getAllPumps());
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

    public Pump fromDto(PumpDto.Request.Patch patchPumpDto, Pump toPatch) {
        if(patchPumpDto == null) {
            return toPatch;
        }
        if(patchPumpDto.getCurrentIngredientId() != null) {
            Ingredient ingredient = ingredientService.getIngredient(patchPumpDto.getCurrentIngredientId());
            if(ingredient == null) {
                throw new IllegalArgumentException("Ingredient with id \"" + toPatch.getCurrentIngredientId() + "\" not found!");
            }
            if(!(ingredient instanceof AutomatedIngredient)) {
                throw new IllegalArgumentException("Ingredient must be an AutomatedIngredient!");
            }
            toPatch.setCurrentIngredient((AutomatedIngredient) ingredient);
        }
        if(patchPumpDto.getIsRemoveIngredient() != null && patchPumpDto.getIsRemoveIngredient()) {
            toPatch.setCurrentIngredient(null);
        }
        BeanUtils.copyProperties(patchPumpDto, toPatch, SpringUtility.getNullPropertyNames(patchPumpDto));
        if(patchPumpDto.getIsPumpedUp() != null) {
            toPatch.setPumpedUp(patchPumpDto.getIsPumpedUp());
        }
        return toPatch;
    }


    //
    // Actions
    //

    public PumpOccupation getPumpOccupation(Pump pump) {
        if(this.cocktailOrderService.isMakingCocktail()) {
            return PumpOccupation.COCKTAIL_PRODUCTION;
        }
        if(this.pumpUpService.isPumpPumpingUp(pump)) {
            return PumpOccupation.PUMPING_UP;
        }
        if(pump.isRunning()) {
            return PumpOccupation.RUNNING;
        }
        return PumpOccupation.NONE;
    }

    public void turnOnOffPumps(boolean turnOn) {
        getAllPumps().forEach(p -> turnOnOffPumpInternal(p, turnOn));
        webSocketService.broadcastPumpLayout(getAllPumps());
    }

    public void turnOnOffPump(Pump pump, boolean turnOn) {
        turnOnOffPumpInternal(pump, turnOn);
        webSocketService.broadcastPumpLayout(getAllPumps());
    }

    private void turnOnOffPumpInternal(Pump pump, boolean turnOn) {
        PumpOccupation occupation = getPumpOccupation(pump);
        if (occupation == PumpOccupation.COCKTAIL_PRODUCTION) {
            throw new IllegalArgumentException("A cocktail is currently being prepared!");
        }
        pumpUpService.cancelPumpUp(pump);
        pump.setRunning(turnOn);
    }

    Set<Long> findIngredientIdsOnPump() {
        return pumpRepository.findIngredientIdsOnPump();
    }

    public enum PumpOccupation {
        RUNNING, PUMPING_UP, COCKTAIL_PRODUCTION, NONE
    }
}
