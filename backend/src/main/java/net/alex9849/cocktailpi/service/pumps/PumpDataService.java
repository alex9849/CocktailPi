package net.alex9849.cocktailpi.service.pumps;

import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.gpio.PinResource;
import net.alex9849.cocktailpi.model.pump.DcPump;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.pump.StepperPump;
import net.alex9849.cocktailpi.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.payload.dto.pump.DcPumpDto;
import net.alex9849.cocktailpi.payload.dto.pump.PumpDto;
import net.alex9849.cocktailpi.payload.dto.pump.StepperPumpDto;
import net.alex9849.cocktailpi.repository.PumpRepository;
import net.alex9849.cocktailpi.service.GpioService;
import net.alex9849.cocktailpi.service.IngredientService;
import net.alex9849.cocktailpi.utils.PinUtils;
import net.alex9849.cocktailpi.utils.SpringUtility;
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
        List<PinResource> pinResources = new ArrayList<>();
        if(pump instanceof DcPump dcPump) {
            if(dcPump.getPin() != null) {
                PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.PUMP, pump.getId(), dcPump.getPin());
            }
        } else if (pump instanceof StepperPump stepperPump) {
            PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.PUMP, pump.getId(), stepperPump.getEnablePin(), stepperPump.getStepPin());
        }
        if(!pinResources.isEmpty()) {
            throw new IllegalArgumentException("BCM-Pin already in use!");
        }
        pump = pumpRepository.create(pump);
        //Turn off pump
        if(pump.isCanPump()) {
            pump.getMotorDriver().shutdown();
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

        if(pump instanceof DcPump dcPump) {
            PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.PUMP, pump.getId(), dcPump.getPin());
        } else if (pump instanceof StepperPump stepperPump) {
            PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.PUMP, pump.getId(), stepperPump.getEnablePin(), stepperPump.getStepPin());
        }


        pumpRepository.update(pump);
        if (beforeUpdate.isCanPump() && pump.isCanPump()){
            if(!Objects.equals(beforeUpdate.getMotorDriver(), pump.getMotorDriver())) {
                beforeUpdate.getMotorDriver().shutdown();
                pump.getMotorDriver().shutdown();
            }
        } else if (beforeUpdate.isCanPump()) {
            beforeUpdate.getMotorDriver().shutdown();
        } else if (pump.isCanPump()) {
            pump.getMotorDriver().shutdown();
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
        pumpRepository.delete(id);
        if(pump.isCanPump()) {
            pump.getMotorDriver().shutdown();
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
        } else {
            if(removeFields.contains("currentIngredient")) {
                toPatch.setCurrentIngredient(null);
            }
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
            return fromDto((StepperPumpDto.Request.Create) patchPumpDto, stepperPump);
        } else if(toPatch instanceof DcPump dcPump) {
            return fromDto((DcPumpDto.Request.Create) patchPumpDto, dcPump);
        }
        return toPatch;
    }

    private Pump fromDto(DcPumpDto.Request.Create patchPumpDto, DcPump toPatchDc) {
        if(patchPumpDto == null) {
            return toPatchDc;
        }
        Set<String> removeFields;
        if(patchPumpDto.getRemoveFields() != null) {
            removeFields = patchPumpDto.getRemoveFields();
        } else {
            removeFields = new HashSet<>();
        }

        if(patchPumpDto.getPin() != null) {
            GpioBoard.Pin pin = gpioService.fromDto(patchPumpDto.getPin());
            toPatchDc.setPin(pin);
        } else {
            if(removeFields.contains("pin")) {
                toPatchDc.setPin(null);
            }
        }

        return toPatchDc;
    }

    private Pump fromDto(StepperPumpDto.Request.Create patchPumpDto, StepperPump toPatchStepper) {
        if(patchPumpDto == null) {
            return toPatchStepper;
        }
        Set<String> removeFields;
        if(patchPumpDto.getRemoveFields() != null) {
            removeFields = patchPumpDto.getRemoveFields();
        } else {
            removeFields = new HashSet<>();
        }

        if(patchPumpDto.getEnablePin() != null) {
            GpioBoard.Pin pin = gpioService.fromDto(patchPumpDto.getEnablePin());
            toPatchStepper.setEnablePin(pin);
        } else {
            if(removeFields.contains("enablePin")) {
                toPatchStepper.setEnablePin(null);
            }
        }

        if(patchPumpDto.getStepPin() != null) {
            GpioBoard.Pin pin = gpioService.fromDto(patchPumpDto.getStepPin());
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
}
