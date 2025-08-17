package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.Glass;
import net.alex9849.cocktailpi.model.LoadCell;
import net.alex9849.cocktailpi.model.pump.LoadCellReader;
import net.alex9849.cocktailpi.model.system.DispensingAreaState;
import net.alex9849.cocktailpi.payload.dto.glass.GlassDto;
import net.alex9849.cocktailpi.payload.dto.system.settings.LoadCellSettingsDto;
import net.alex9849.cocktailpi.repository.GlassRepository;
import net.alex9849.cocktailpi.service.pumps.PumpLockService;
import net.alex9849.motorlib.exception.HX711Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
public class GlassService {
    private DispensingAreaState dispensingAreaState = new DispensingAreaState();

    @Autowired
    private GlassRepository glassRepository;
    @Autowired
    private LoadCellService loadCellService;
    @Autowired
    private WebSocketService webSocketService;

    private void setDispensingAreaState(DispensingAreaState newState) {
        if (!Objects.equals(dispensingAreaState, newState)) {
            dispensingAreaState = newState;
            webSocketService.broadcastDetectedGlass(dispensingAreaState);
        }
    }

    @Scheduled(fixedDelay = 300)
    void updateCurrentGlass() {
        DispensingAreaState newState = new DispensingAreaState();
        newState.setAreaEmpty(false);
        newState.setGlass(null);
        LoadCellSettingsDto.Duplex.DispensingArea daSettings = loadCellService.getDispensingAreaSettings();
        if (!daSettings.isMatchGlass() && !daSettings.isCheckGlassPlaced()) {
            setDispensingAreaState(newState);
            return;
        }

        long measuredWeight;
        try {
            LoadCell loadCell = loadCellService.getLoadCell();
            if (loadCell == null || !loadCell.isCalibrated()) {
                setDispensingAreaState(newState);
                return;
            }
            LoadCellReader reader = loadCell.getLoadCellReader();
            if (reader == null) {
                setDispensingAreaState(newState);
                return;
            }
            measuredWeight = reader.readCurrent().get();
        } catch (InterruptedException | ExecutionException | HX711Exception e) {
            setDispensingAreaState(newState);
            return;
        }
        if(daSettings.isCheckGlassPlaced()) {
            newState.setAreaEmpty(measuredWeight < 20);
        }
        if(daSettings.isMatchGlass()) {
            newState.setGlass(getGlassByWeight(measuredWeight));
        }
        setDispensingAreaState(newState);
    }

    public synchronized DispensingAreaState getDispensingAreaState() {
        return dispensingAreaState;
    }

    private Glass getGlassByWeight(long weight) {
        Glass foundGlass = null;
        int currentWeightDiff = Integer.MAX_VALUE;
        for (Glass glass : getAll()) {
            if(glass.getEmptyWeight() == null) {
                continue;
            }
            int lowerBound = glass.getEmptyWeight() - 5;
            int upperBound = glass.getEmptyWeight() + 15;
            if (lowerBound <= weight && weight <= upperBound) {
                int weightDiff = (int) Math.abs(weight - glass.getEmptyWeight());
                if(foundGlass == null || currentWeightDiff > weightDiff) {
                    foundGlass = glass;
                    currentWeightDiff = weightDiff;
                }
            }
        }
        return foundGlass;
    }


    public List<Glass> getAll() {
        return glassRepository.findByIds(glassRepository.findAllIds().toArray(Long[]::new));
    }

    public Glass getById(long id) {
        return glassRepository.findById(id).orElse(null);
    }

    public Glass getByName(String name) {
        Set<Long> idsWithName = glassRepository.findIdsByName(name);
        if (idsWithName.isEmpty()) {
            return null;
        }
        return glassRepository.findById(idsWithName.iterator().next()).orElse(null);
    }

    public Glass createGlass(Glass glass) {
        if(!glassRepository.findIdsByName(glass.getName()).isEmpty()) {
            throw new IllegalArgumentException("A glass with the name " + glass.getName() + " already exists!");
        }
        return glassRepository.create(glass);
    }

    public Glass updateGlass(Glass glass) {
        Set<Long> idsWithName = glassRepository.findIdsByName(glass.getName());
        if(!idsWithName.isEmpty() && !idsWithName.contains(glass.getId())) {
            throw new IllegalArgumentException("A glass with the name " + glass.getName() + " already exists!");
        }
        glassRepository.update(glass);
        return glass;
    }

    public boolean deleteGlass(long id) {
        return glassRepository.delete(id);
    }

    public Glass getIngredientRecipeDefaultGlass() {
        Long siGlassId = glassRepository.getSingleIngredientGlassId();
        if(siGlassId == null) {
            return null;
        }
        return glassRepository.findById(siGlassId).orElse(null);
    }

    public Glass fromDto(GlassDto.Duplex.Detailed glassDto) {
        Glass glass = new Glass();
        glass.setSize(glassDto.getSize());
        glass.setName(glassDto.getName());
        glass.setId(glassDto.getId());
        glass.setEmptyWeight(glassDto.getEmptyWeight());
        glass.setUseForSingleIngredients(glassDto.isUseForSingleIngredients());
        glass.setDefault(glassDto.isDefault());
        return glass;
    }
}
