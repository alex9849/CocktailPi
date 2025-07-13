package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.Glass;
import net.alex9849.cocktailpi.model.LoadCell;
import net.alex9849.cocktailpi.payload.dto.glass.GlassDto;
import net.alex9849.cocktailpi.repository.GlassRepository;
import net.alex9849.cocktailpi.service.pumps.PumpLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class GlassService {
    private Glass currentGlass = null;

    @Autowired
    private GlassRepository glassRepository;
    @Autowired
    private LoadCellService loadCellService;
    @Autowired
    private WebSocketService webSocketService;

    @Scheduled(fixedDelay = 500)
    void updateCurrentGlass() {
        try {
            LoadCell loadcell = loadCellService.getLoadCell();
            if(loadcell == null) {
                return;
            }
            if(!loadcell.isCalibrated()) {
                return;
            }
            long weight = loadcell.getHX711().read(7);
            Glass foundGlass = getGlass(weight);
            if(foundGlass == null && currentGlass == null) {
                return;
            }
            if(foundGlass != null && currentGlass != null && currentGlass.getId() == foundGlass.getId()) {
                return;
            }
            synchronized (this) {
                currentGlass = foundGlass;
                webSocketService.broadcastDetectedGlass(currentGlass);
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    public synchronized Glass getDetectedGlass() {
        return currentGlass;
    }

    private Glass getGlass(long weight) {
        Glass foundGlass = null;
        int currentWeightDiff = Integer.MAX_VALUE;
        for (Glass glass : getAll()) {
            if(glass.getEmptyWeight() == null) {
                continue;
            }
            int lowerBound = glass.getEmptyWeight() - 10;
            int upperBound = glass.getEmptyWeight() + 10;
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
