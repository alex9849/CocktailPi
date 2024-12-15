package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.LoadCell;
import net.alex9849.cocktailpi.model.gpio.PinResource;
import net.alex9849.cocktailpi.payload.dto.system.settings.LoadCellDto;
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
    private boolean checkedIfLoadCellPersisted = false;
    private static final String REPO_KEY_LOAD_CELL_ENABLED = "LC_Enabled";
    public static final String REPO_KEY_LOAD_CELL_DT_PIN = "LC_DT";
    public static final String REPO_KEY_LOAD_CELL_CLK_PIN = "LC_SCK";
    private static final String REPO_KEY_LOAD_CELL_ZERO_VALUE = "LC_ZERO_VALUE";
    private static final String REPO_KEY_LOAD_CELL_REFERENCE_VALUE = "LC_REF_VALUE";
    private static final String REPO_KEY_LOAD_CELL_REFERENCE_WEIGHT = "LC_REF_WEIGHT";

    @Autowired
    private PumpLockService lockService;

    @Autowired
    private OptionsRepository optionsRepository;

    @Autowired
    private GpioService gpioService;
    @Autowired
    private PumpService pumpService;


    public LoadCell calibrateLoadCellZero() {
        LoadCell loadCell = getLoadCell();
        if(loadCell == null) {
            throw new IllegalStateException("Load cell not configured!");
        }
        try {
            loadCell.getHX711().calibrateEmpty(7);
        } catch (InterruptedException e) {
            throw new RuntimeException("Reading load cell has been interrupted!", e);
        }
        loadCell.setZeroForceValue(getLoadCell().getHX711().emptyValue);
        setLoadCell(loadCell);
        return getLoadCell();
    }

    public LoadCell calibrateRefValue(long referenceWeight) {
        LoadCell loadCell = getLoadCell();
        if(loadCell == null) {
            throw new IllegalStateException("Load cell not configured!");
        }
        try {
            loadCell.getHX711().calibrateWeighted(referenceWeight, 7);
        } catch (InterruptedException e) {
            throw new RuntimeException("Reading load cell has been interrupted!", e);
        }
        loadCell.setReferenceForceValue(getLoadCell().getHX711().calibrationValue);
        loadCell.setReferenceForceValueWeight(referenceWeight);
        setLoadCell(loadCell);
        return getLoadCell();
    }

    public long readLoadCell() {
        LoadCell loadCell = getLoadCell();
        if(loadCell == null) {
            throw new IllegalStateException("Load cell not configured!");
        }
        if(!loadCell.isCalibrated()) {
            throw new IllegalStateException("Load cell not calibrated!");
        }
        try {
            return loadCell.getHX711().read(7);
        } catch (InterruptedException e) {
            throw new RuntimeException("Reading load cell has been interrupted!", e);
        }
    }

    public void setLoadCell(LoadCell loadCell) {
        if (!lockService.testAndAcquireGlobal(this)) {
            throw new IllegalArgumentException("Some pumps are currently occupied!");
        }
        try {
            optionsRepository.setOption(REPO_KEY_LOAD_CELL_ENABLED, Boolean.valueOf(loadCell != null).toString());
            if(loadCell == null) {
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_CLK_PIN, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_DT_PIN, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_ZERO_VALUE, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_REFERENCE_VALUE, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_REFERENCE_WEIGHT, false);
            } else {
                LoadCell old = getLoadCell();
                if(old != null) {
                    loadCell.setZeroForceValue(old.getZeroForceValue());
                    loadCell.setReferenceForceValue(old.getReferenceForceValue());
                    loadCell.setReferenceForceValueWeight(old.getReferenceForceValueWeight());
                }

                PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.LOAD_CELL, null, loadCell.getClkPin(), loadCell.getDtPin());
                optionsRepository.setPinOption(REPO_KEY_LOAD_CELL_CLK_PIN, loadCell.getClkPin());
                optionsRepository.setPinOption(REPO_KEY_LOAD_CELL_DT_PIN, loadCell.getDtPin());
                optionsRepository.setOption(REPO_KEY_LOAD_CELL_ZERO_VALUE, String.valueOf(loadCell.getZeroForceValue()));
                if(loadCell.getReferenceForceValue() != null) {
                    optionsRepository.setOption(REPO_KEY_LOAD_CELL_REFERENCE_VALUE, String.valueOf(loadCell.getReferenceForceValue()));
                }
                if(loadCell.getReferenceForceValueWeight() != null) {
                    optionsRepository.setOption(REPO_KEY_LOAD_CELL_REFERENCE_WEIGHT, String.valueOf(loadCell.getReferenceForceValueWeight()));
                }
            }
            checkedIfLoadCellPersisted = false;
            reloadLoadCell();
            pumpService.broadCastPumpLayout();
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
        if(loadCell == null && !checkedIfLoadCellPersisted) {
            reloadLoadCell();
            checkedIfLoadCellPersisted = true;
        }
        return loadCell;
    }

    public LoadCell fromDto(LoadCellDto.Request.Create dto) {
        if(dto == null) {
            return null;
        }
        LoadCell loadCell = new LoadCell();
        loadCell.setDtPin(gpioService.fromDto(dto.getDtPin()));
        loadCell.setClkPin(gpioService.fromDto(dto.getClkPin()));
        return loadCell;
    }
}
