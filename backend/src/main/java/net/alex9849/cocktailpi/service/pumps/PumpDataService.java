package net.alex9849.cocktailpi.service.pumps;

import net.alex9849.cocktailpi.model.gpio.LocalPin;
import net.alex9849.cocktailpi.model.gpio.Pin;
import net.alex9849.cocktailpi.model.gpio.PinResource;
import net.alex9849.cocktailpi.model.pump.*;
import net.alex9849.cocktailpi.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.payload.dto.pump.*;
import net.alex9849.cocktailpi.repository.PumpRepository;
import net.alex9849.cocktailpi.service.GpioService;
import net.alex9849.cocktailpi.service.IngredientService;
import net.alex9849.cocktailpi.service.SystemService;
import net.alex9849.cocktailpi.utils.PinUtils;
import net.alex9849.cocktailpi.utils.SpringUtility;
import net.alex9849.motorlib.pin.PinState;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class PumpDataService {

    @Autowired
    private PumpRepository pumpRepository;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private GpioService gpioService;
    @Autowired
    private SystemService systemService;

    //
    // CRUD actions
    //
    public List<Pump> getAllPumps() {
        return pumpRepository.findAll();
    }

    public List<Pump> getAllCompletedPumps() {
        return getAllPumps().stream().filter(Pump::isCompleted).toList();
    }

    public Pump getPump(long id) {
        return pumpRepository.findById(id).orElse(null);
    }

    public Pump createPump(Pump pump) {
        if(pump instanceof DcPump dcPump) {
            PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.PUMP, pump.getId(), dcPump.getPin());
        } else if (pump instanceof StepperPump stepperPump) {
            PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.PUMP, pump.getId(), stepperPump.getEnablePin(), stepperPump.getStepPin());
        } else if (pump instanceof Valve valve) {
            PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.PUMP, pump.getId(), valve.getPin());
        }
        pump = pumpRepository.create(pump);
        updateDefaultPinState(null, pump);
        //Turn off pump
        if(pump.isCanPump()) {
            pump.shutdownDriver();
        }
        return pump;
    }
    public Pump updatePump(Pump pump) {
        Optional<Pump> oBeforeUpdate = pumpRepository.findById(pump.getId());
        if(oBeforeUpdate.isEmpty()) {
            throw new IllegalArgumentException("Pump doesn't exist!");
        }
        if(!oBeforeUpdate.get().getClass().equals(pump.getClass())) {
            throw new IllegalArgumentException("Can't change pump type!");
        }
        Pump beforeUpdate = oBeforeUpdate.get();

        if(pump instanceof OnOffPump onOffPump) {
            PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.PUMP, pump.getId(), onOffPump.getPin());
        } else if (pump instanceof StepperPump stepperPump) {
            PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.PUMP, pump.getId(), stepperPump.getEnablePin(), stepperPump.getStepPin());
        }

        updateDefaultPinState(beforeUpdate, pump);
        pumpRepository.update(pump);
        if (beforeUpdate.isCanPump() && pump.isCanPump()){
            if(!Objects.equals(beforeUpdate.getMotorDriver(), pump.getMotorDriver())) {
                beforeUpdate.shutdownDriver();
                pump.shutdownDriver();
            }
        } else if (beforeUpdate.isCanPump()) {
            beforeUpdate.shutdownDriver();
        } else if (pump.isCanPump()) {
            pump.shutdownDriver();
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
        updateDefaultPinState(pump, null);
        pumpRepository.delete(id);
        if(pump.isCanPump()) {
            pump.shutdownDriver();
        }
    }

    public Pump fromDto(PumpDto.Request.Create pumpDto) {
        if(pumpDto == null) {
            return null;
        }
        if(pumpDto instanceof DcPumpDto.Request.Create) {
            return fromDto(pumpDto, new DcPump());
        } else if (pumpDto instanceof StepperPumpDto.Request.Create) {
            return fromDto(pumpDto, new StepperPump());
        } else if (pumpDto instanceof ValveDto.Request.Create) {
            return fromDto(pumpDto, new Valve());
        } else {
            throw new IllegalStateException("Unknown pumpDto-type: " + pumpDto.getClass().getName());
        }
    }

    public Pump fromDto(PumpDto.Request.Create patchPumpDto, Pump toPatch) {
        if(patchPumpDto == null) {
            return toPatch;
        }
        Set<String> removeFields;
        if(patchPumpDto.getRemoveFields() != null) {
            removeFields = patchPumpDto.getRemoveFields();
        } else {
            removeFields = new HashSet<>();
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
        } else if(removeFields.contains("currentIngredient")) {
            toPatch.setCurrentIngredient(null);
        }

        String[] ignoreFields = SpringUtility.getNullPropertyNames(patchPumpDto);
        ignoreFields = Arrays.stream(ignoreFields)
                .filter(x -> !removeFields.contains(x))
                .toArray(String[]::new);
        BeanUtils.copyProperties(patchPumpDto, toPatch, ignoreFields);

        if(patchPumpDto.getIsPumpedUp() != null) {
            toPatch.setPumpedUp(patchPumpDto.getIsPumpedUp());
        }

        if(toPatch instanceof StepperPump stepperPump) {
            return fromDto((StepperPumpDto.Request.Create) patchPumpDto, stepperPump, removeFields);
        } else if(toPatch instanceof OnOffPump onOffPump) {
            return fromDto((OnOffPumpDto.Request.Create) patchPumpDto, onOffPump, removeFields);
        }
        return toPatch;
    }

    private Pump fromDto(OnOffPumpDto.Request.Create patchPumpDto, OnOffPump toPatchOnOff, Set<String> removeFields) {
        if(toPatchOnOff == null) {
            return toPatchOnOff;
        }

        if(patchPumpDto.getPin() != null) {
            Pin pin = gpioService.fromDto(patchPumpDto.getPin());
            toPatchOnOff.setPin(pin);
        } else if(removeFields.contains("pin")) {
            toPatchOnOff.setPin(null);
        }

        return toPatchOnOff;
    }

    private Pump fromDto(StepperPumpDto.Request.Create patchPumpDto, StepperPump toPatchStepper, Set<String> removeFields) {
        if(patchPumpDto == null) {
            return toPatchStepper;
        }

        if(patchPumpDto.getEnablePin() != null) {
            Pin pin = gpioService.fromDto(patchPumpDto.getEnablePin());
            toPatchStepper.setEnablePin(pin);
        } else {
            if(removeFields.contains("enablePin")) {
                toPatchStepper.setEnablePin(null);
            }
        }

        if(patchPumpDto.getStepPin() != null) {
            Pin pin = gpioService.fromDto(patchPumpDto.getStepPin());
            toPatchStepper.setStepPin(pin);
        } else {
            if(removeFields.contains("stepPin")) {
                toPatchStepper.setStepPin(null);
            }
        }

        return toPatchStepper;
    }

    public Set<Long> findIngredientIdsOnPump() {
        return pumpRepository.findIngredientIdsOnPump();
    }

    public List<Pump> findPumpsWithIngredient(long ingredientId) {
        return pumpRepository.findPumpsWithIngredient(ingredientId);
    }

    private void updateDefaultPinState(Pump oldPump, Pump newPump) {
        if(oldPump != null && newPump != null) {
            if(!oldPump.getClass().equals(newPump.getClass())) {
                throw new IllegalArgumentException("Pumps need to have the same class!");
            }
        }
        if((newPump instanceof OnOffPump) || (oldPump instanceof OnOffPump)) {
            OnOffPump ooOld = (OnOffPump) oldPump;
            OnOffPump ooNew = (OnOffPump) newPump;
            Pin before = null;
            Pin after = null;
            Boolean beforeDefaultStateLow = null;
            Boolean afterDefaultStateLow = null;
            if(oldPump != null) {
                before = ooOld.getPin();
                beforeDefaultStateLow = ooOld.isPowerStateHigh();
            }
            if(newPump != null) {
                after = ooNew.getPin();
                afterDefaultStateLow = ooNew.isPowerStateHigh();
            }
            updateDefaultPinState(before, after, beforeDefaultStateLow, afterDefaultStateLow);
            return;
        }
        if((newPump instanceof StepperPump) || (oldPump instanceof StepperPump)) {
            StepperPump stepOld = (StepperPump) oldPump;
            StepperPump stepNew = (StepperPump) newPump;
            Pin beforeStep = null;
            Pin beforeEnable = null;
            Pin afterStep = null;
            Pin afterEnable = null;
            if(stepOld != null) {
                beforeStep = stepOld.getStepPin();
                beforeEnable = stepOld.getEnablePin();
            }
            if(stepNew != null) {
                afterStep = stepNew.getStepPin();
                afterEnable = stepNew.getEnablePin();
            }
            updateDefaultPinState(beforeEnable, afterEnable, false, false);
            updateDefaultPinState(beforeStep, afterStep, false, false);
            return;
        }
    }

    private void updateDefaultPinState(Pin before, Pin after, Boolean oldDefaultStateLow, Boolean newDefaultStateLow) {
        if (!(before instanceof LocalPin)) {
            before = null;
        }
        if (!(after instanceof LocalPin)) {
            after = null;
        }
        if(after != null && before != null) {
            if(before.getPinNr() == after.getPinNr()) {
                if (oldDefaultStateLow == newDefaultStateLow) {
                    return;
                }
                updateDefaultPinState((LocalPin) after, newDefaultStateLow, false);
                return;
            }
        }
        updateDefaultPinState((LocalPin) before, newDefaultStateLow, true);
        updateDefaultPinState((LocalPin) after, newDefaultStateLow, false);
    }

    private void updateDefaultPinState(LocalPin pin, Boolean defaultStateLow, boolean delete) {
        if(pin == null) {
            return;
        }
        if (delete || (defaultStateLow == null)) {
            systemService.setPinBootState(pin, null);
            return;
        }
        systemService.setPinBootState(pin, defaultStateLow ? PinState.LOW : PinState.HIGH);
    }
}
