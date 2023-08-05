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
import net.alex9849.cocktailmaker.service.IngredientService;
import net.alex9849.cocktailmaker.utils.SpringUtility;
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
        List<Integer> pinList = new ArrayList<>();
        if(pump instanceof DcPump dcPump && dcPump.getPin() != null) {
            pinList.add(dcPump.getPin());
        } else if (pump instanceof StepperPump stepperPump) {
            if(stepperPump.getEnablePin() != null)
                pinList.add(stepperPump.getEnablePin());
            if(stepperPump.getStepPin() != null)
                pinList.add(stepperPump.getStepPin());
        }
        for(Integer pin : pinList) {
            Optional<Pump> oPump = pumpRepository.findByBcmPin(pin);
            if(oPump.isPresent()) {
                if(oPump.get().getId() != pump.getId()) {
                    throw new IllegalArgumentException("BCM-Pin already in use!");
                }
            }
            //TODO check if pin already in use by other services
        }
        pump = pumpRepository.create(pump);
        //Turn off pump
        if(pump.isCanPump()) {
            pump.getMotorDriver().shutdown();
        }
        return pump;
    }
    public Pump updatePump(Pump pump) {
        Optional<Pump> beforeUpdate = pumpRepository.findById(pump.getId());
        if(!beforeUpdate.isPresent()) {
            throw new IllegalArgumentException("Pump doesn't exist!");
        }
        if(!beforeUpdate.get().getClass().equals(pump.getClass())) {
            throw new IllegalArgumentException("Can't change pump type!");
        }

        List<Integer> pinList = new ArrayList<>();
        if(pump instanceof DcPump dcPump) {
            if(dcPump.getPin() != null) {
                pinList.add(dcPump.getPin());
            }
        } else if (pump instanceof StepperPump stepperPump) {
            if(stepperPump.getEnablePin() != null) {
                pinList.add(stepperPump.getEnablePin());
            }
            if(stepperPump.getStepPin() != null) {
                pinList.add(stepperPump.getStepPin());
            }
        }
        for(Integer pin : pinList) {
            Optional<Pump> oPump = pumpRepository.findByBcmPin(pin);
            if(oPump.isPresent()) {
                if(oPump.get().getId() != pump.getId()) {
                    throw new IllegalArgumentException("BCM-Pin already in use!");
                }
            }
            //TODO check if pin already in use by other services
        }
        if(beforeUpdate.get().isCanPump()) {
            beforeUpdate.get().getMotorDriver().shutdown();
        }
        pumpRepository.update(pump);
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
        return toPatch;
    }

    public Set<Long> findIngredientIdsOnPump() {
        return pumpRepository.findIngredientIdsOnPump();
    }
}
