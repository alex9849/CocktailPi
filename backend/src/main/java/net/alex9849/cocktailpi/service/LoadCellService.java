package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.LoadCell;
import net.alex9849.cocktailpi.model.gpio.PinResource;
import net.alex9849.cocktailpi.repository.OptionsRepository;
import net.alex9849.cocktailpi.service.pumps.PumpLockService;
import net.alex9849.cocktailpi.utils.PinUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoadCellService {
    private LoadCell loadCell = null;
    private final String REPO_KEY_LOAD_CELL_ENABLED = "LC_Enabled";
    private final String REPO_KEY_LOAD_CELL_DT_PIN = "LC_DT";
    private final String REPO_KEY_LOAD_CELL_CLK_PIN = "LC_SCK";
    private final String REPO_KEY_LOAD_CELL_ZERO_VALUE = "LC_ZERO_VALUE";
    private final String REPO_KEY_LOAD_CELL_REFERENCE_VALUE = "LC_REF_VALUE";
    private final String REPO_KEY_LOAD_CELL_REFERENCE_WEIGHT = "LC_REF_WEIGHT";

    @Autowired
    private PumpLockService lockService;

    @Autowired
    private OptionsRepository optionsRepository;

    public LoadCell calibrateLoadCellZero() {
        LoadCell loadCell = getLoadCell();
        if(loadCell == null) {
            throw new IllegalStateException("Load cell not configured!");
        }
        loadCell.getHX711().calibrateEmpty(0);
        loadCell.setZeroForceValue(getLoadCell().getHX711().emptyValue);
        setLoadCell(loadCell);
        return getLoadCell();
    }

    public LoadCell calibrateRefValue(long referenceWeight) {
        LoadCell loadCell = getLoadCell();
        if(loadCell == null) {
            throw new IllegalStateException("Load cell not configured!");
        }
        loadCell.getHX711().calibrateWeighted(referenceWeight);
        loadCell.setReferenceForceValueWeight(referenceWeight);
        loadCell.setReferenceForceValue(getLoadCell().getHX711().calibrationValue);
        setLoadCell(loadCell);
        return getLoadCell();
    }

    public long readLoadCell() {
        LoadCell loadCell = getLoadCell();
        if(loadCell == null) {
            throw new IllegalStateException("Load cell not configured!");
        }
        if(loadCell.isCalibrated()) {
            throw new IllegalStateException("Load cell not calibrated!");
        }
        return loadCell.getHX711().read();
    }

    public void setLoadCell(LoadCell loadCell) {
        if (!lockService.testAndAcquireGlobal(this)) {
            throw new IllegalArgumentException("Some pumps are currently occupied!");
        }
        try {
            optionsRepository.setOption(REPO_KEY_LOAD_CELL_ENABLED, Boolean.valueOf(loadCell == null).toString());
            if(loadCell == null) {
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_CLK_PIN, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_DT_PIN, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_ZERO_VALUE, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_REFERENCE_VALUE, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_REFERENCE_WEIGHT, false);
            } else {
                PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.LOAD_CELL, null, loadCell.getClkPin());
                PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.LOAD_CELL, null, loadCell.getClkPin());
                optionsRepository.setPinOption(REPO_KEY_LOAD_CELL_CLK_PIN, loadCell.getClkPin());
                optionsRepository.setPinOption(REPO_KEY_LOAD_CELL_DT_PIN, loadCell.getDtPin());
            }
            reloadLoadCell();
        } finally {
            lockService.releaseGlobal(this);
        }
    }

    private void reloadLoadCell() {
        boolean enabled = Boolean.parseBoolean(optionsRepository.getOption(REPO_KEY_LOAD_CELL_ENABLED).orElse(null));
        if(!enabled) {
            loadCell = null;
            return;
        }
        LoadCell loadCell = new LoadCell();
        loadCell.setClkPin(optionsRepository.getPinOption(REPO_KEY_LOAD_CELL_CLK_PIN).orElse(null));
        loadCell.setDtPin(optionsRepository.getPinOption(REPO_KEY_LOAD_CELL_DT_PIN).orElse(null));
        optionsRepository.getOption(REPO_KEY_LOAD_CELL_ZERO_VALUE)
                .ifPresent(s -> loadCell.setZeroForceValue(Long.parseLong(s)));
        optionsRepository.getOption(REPO_KEY_LOAD_CELL_REFERENCE_VALUE)
                .ifPresent(s -> loadCell.setReferenceForceValue(Long.parseLong(s)));
        optionsRepository.getOption(REPO_KEY_LOAD_CELL_REFERENCE_WEIGHT)
                .ifPresent(s -> loadCell.setReferenceForceValueWeight(Long.parseLong(s)));
        this.loadCell = loadCell;
    }

    public LoadCell getLoadCell() {
        if(loadCell == null) {
            reloadLoadCell();
        }
        return loadCell;
    }
}
