package net.alex9849.cocktailmaker.service.pumps;

import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.model.pump.StepperPump;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.payload.dto.pump.DcPumpDto;
import net.alex9849.cocktailmaker.payload.dto.pump.PumpDto;
import net.alex9849.cocktailmaker.payload.dto.pump.StepperPumpDto;
import net.alex9849.cocktailmaker.repository.PumpRepository;
import net.alex9849.cocktailmaker.service.CocktailOrderService;
import net.alex9849.cocktailmaker.service.IngredientService;
import net.alex9849.cocktailmaker.service.WebSocketService;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private PumpLockService pumpLockService;

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
        List<Integer> pinList = new ArrayList<>();
        if(pump instanceof DcPump) {
            pinList.add(((DcPump) pump).getBcmPin());
        } else if (pump instanceof StepperPump) {
            pinList.add(((StepperPump) pump).getEnablePin());
            pinList.add(((StepperPump) pump).getStepPin());
        }
        for(Integer pin : pinList) {
            Optional<Pump> oPump = pumpRepository.findByBcmPin(pin);
            if(oPump.isPresent()) {
                if(oPump.get().getId() != pump.getId()) {
                    throw new IllegalArgumentException("BCM-Pin already in use!");
                }
            }
            if(pumpUpService.isGpioInUseAdVdPin(pin)) {
                throw new IllegalArgumentException("BCM-Pin is already used as a voltage director!");
            }
        }
        pump = pumpRepository.create(pump);
        //Turn off pump
        pump.setRunning(false);
        webSocketService.broadcastPumpLayout(getAllPumps());
        return pump;
    }

    public Set<Pump> updatePumps(Set<Pump> pumps, boolean isSourceCocktailfactory) {
        Set<Pump> updated = new HashSet<>();
        for(Pump pump : pumps) {
            updated.add(this.internalUpdatePump(pump, isSourceCocktailfactory));
        }
        webSocketService.broadcastPumpLayout(getAllPumps());
        return updated;
    }

    public Pump updatePump(Pump pump) {
        Pump updated = this.internalUpdatePump(pump, false);
        webSocketService.broadcastPumpLayout(getAllPumps());
        return updated;
    }

    private Pump internalUpdatePump(Pump pump, boolean isSourceCocktailfactory) {
        Optional<Pump> beforeUpdate = pumpRepository.findById(pump.getId());
        if(!beforeUpdate.isPresent()) {
            throw new IllegalArgumentException("Pump doesn't exist!");
        }
        if(!beforeUpdate.get().getClass().equals(pump.getClass())) {
            throw new IllegalArgumentException("Can't change pump type!");
        }
        if(getPumpOccupation(beforeUpdate.get()) != PumpOccupation.NONE && !isSourceCocktailfactory) {
            throw new IllegalStateException("Pump currently occupied!");
        }

        List<Integer> pinList = new ArrayList<>();
        if(pump instanceof DcPump) {
            pinList.add(((DcPump) pump).getBcmPin());
        } else if (pump instanceof StepperPump) {
            pinList.add(((StepperPump) pump).getEnablePin());
            pinList.add(((StepperPump) pump).getStepPin());
        }
        for(Integer pin : pinList) {
            Optional<Pump> oPump = pumpRepository.findByBcmPin(pin);
            if(oPump.isPresent()) {
                if(oPump.get().getId() != pump.getId()) {
                    throw new IllegalArgumentException("BCM-Pin already in use!");
                }
            }
            if(pumpUpService.isGpioInUseAdVdPin(pin)) {
                throw new IllegalArgumentException("BCM-Pin is already used as a voltage director!");
            }
        }
        pumpRepository.update(pump);
        beforeUpdate.get().setRunning(false);
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

    public Pump fromDto(PumpDto.Request.Patch pumpDto) {
        if(pumpDto == null) {
            return null;
        }
        if(pumpDto instanceof DcPumpDto.Request.Patch) {
            return fromDto(pumpDto, new DcPump());
        } else if (pumpDto instanceof StepperPumpDto.Request.Patch) {
            return fromDto(pumpDto, new StepperPump());
        } else {
            throw new IllegalStateException("Unknown pumpDto-type: " + pumpDto.getClass().getName());
        }
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
        pumpUpService.updateReversePumpSettingsCountdown();
    }

    public Set<Long> findIngredientIdsOnPump() {
        return pumpRepository.findIngredientIdsOnPump();
    }

    public enum PumpOccupation {
        RUNNING, PUMPING_UP, COCKTAIL_PRODUCTION, NONE
    }
}
