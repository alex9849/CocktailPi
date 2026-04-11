package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.LoadCell;
import net.alex9849.cocktailpi.model.gpio.PinResource;
import net.alex9849.cocktailpi.payload.dto.system.settings.LoadCellSettingsDto;
import net.alex9849.cocktailpi.repository.OptionsRepository;
import net.alex9849.cocktailpi.service.pumps.PumpLockService;
import net.alex9849.cocktailpi.utils.PinUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoadCellService {
    private LoadCell loadCell = null;
    private LoadCellSettingsDto.Duplex.DispensingArea dispensingAreaSettings = null;
    private boolean checkedIfLoadCellPersisted = false;
    private static final String REPO_KEY_LOAD_CELL_ENABLED = "LC_Enabled";
    public static final String REPO_KEY_LOAD_CELL_DT_PIN = "LC_DT";
    public static final String REPO_KEY_LOAD_CELL_CLK_PIN = "LC_SCK";
    private static final String REPO_KEY_LOAD_CELL_ZERO_VALUE = "LC_ZERO_VALUE";
    private static final String REPO_KEY_LOAD_CELL_REFERENCE_VALUE = "LC_REF_VALUE";
    private static final String REPO_KEY_LOAD_CELL_REFERENCE_WEIGHT = "LC_REF_WEIGHT";
    private static final String REPO_KEY_LOAD_CELL_CHECK_GLASS_PLACED = "LC_CHECK_GLASS_PLACED";
    private static final String REPO_KEY_LOAD_CELL_MATCH_GLASS = "LC_MATCH_GLASS";

    @Autowired
    private PumpLockService lockService;
    @Autowired
    private OptionsRepository optionsRepository;
    @Autowired
    private GpioService gpioService;
    @Autowired
    private PumpService pumpService;
    @Value("${alex9849.app.demoMode}")
    private boolean isDemoMode;


    public LoadCell calibrateLoadCellZero() {
        LoadCell loadCell = getLoadCell();
        if (loadCell == null) {
            throw new IllegalStateException("Load cell not configured!");
        }
        try {
            loadCell.getHX711().calibrateEmpty(7);
        } catch (InterruptedException e) {
            throw new RuntimeException("Reading load cell has been interrupted!", e);
        }
        loadCell.setZeroForceValue(getLoadCell().getHX711().emptyValue);
        setLoadCell(loadCell, getDispensingAreaSettings());
        return getLoadCell();
    }

    public LoadCell calibrateRefValue(long referenceWeight) {
        LoadCell loadCell = getLoadCell();
        if (loadCell == null) {
            throw new IllegalStateException("Load cell not configured!");
        }
        try {
            loadCell.getHX711().calibrateWeighted(referenceWeight, 7);
        } catch (InterruptedException e) {
            throw new RuntimeException("Reading load cell has been interrupted!", e);
        }
        loadCell.setReferenceForceValue(getLoadCell().getHX711().calibrationValue);
        loadCell.setReferenceForceValueWeight(referenceWeight);
        setLoadCell(loadCell, getDispensingAreaSettings());
        return getLoadCell();
    }

    public long readLoadCell() {
        LoadCell loadCell = getLoadCell();
        if (loadCell == null) {
            throw new IllegalStateException("Load cell not configured!");
        }
        if (!loadCell.isCalibrated()) {
            throw new IllegalStateException("Load cell not calibrated!");
        }
        try {
            return loadCell.getHX711().read(7);
        } catch (InterruptedException e) {
            throw new RuntimeException("Reading load cell has been interrupted!", e);
        }
    }

    public LoadCellSettingsDto.Duplex.DispensingArea getDispensingAreaSettings() {
        if (loadCell == null && !checkedIfLoadCellPersisted) {
            reloadLoadCell();
            checkedIfLoadCellPersisted = true;
        }
        return dispensingAreaSettings;
    }

    public void setLoadCell(LoadCell loadCell, LoadCellSettingsDto.Duplex.DispensingArea dispensingAreaSettings) {
        if (!lockService.testAndAcquireGlobal(this)) {
            throw new IllegalArgumentException("Some pumps are currently occupied!");
        }
        try {
            if(isDemoMode) {
                throw new IllegalArgumentException("Modifying load cell settings is not allowed in demomode!");
            }
            optionsRepository.setOption(REPO_KEY_LOAD_CELL_ENABLED, Boolean.valueOf(loadCell != null).toString());
            if (loadCell == null) {
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_CLK_PIN, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_DT_PIN, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_ZERO_VALUE, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_REFERENCE_VALUE, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_REFERENCE_WEIGHT, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_CHECK_GLASS_PLACED, false);
                optionsRepository.delOption(REPO_KEY_LOAD_CELL_MATCH_GLASS, false);
            } else {
                LoadCell old = getLoadCell();
                if (old != null) {
                    loadCell.setZeroForceValue(old.getZeroForceValue());
                    loadCell.setReferenceForceValue(old.getReferenceForceValue());
                    loadCell.setReferenceForceValueWeight(old.getReferenceForceValueWeight());
                }

                PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.LOAD_CELL, null, loadCell.getClkHwPin(), loadCell.getDtHwPin());
                optionsRepository.setPinOption(REPO_KEY_LOAD_CELL_CLK_PIN, loadCell.getClkHwPin());
                optionsRepository.setPinOption(REPO_KEY_LOAD_CELL_DT_PIN, loadCell.getDtHwPin());
                optionsRepository.setOption(REPO_KEY_LOAD_CELL_ZERO_VALUE, String.valueOf(loadCell.getZeroForceValue()));
                optionsRepository.setOption(REPO_KEY_LOAD_CELL_CHECK_GLASS_PLACED, String.valueOf(dispensingAreaSettings.isCheckGlassPlaced()));
                optionsRepository.setOption(REPO_KEY_LOAD_CELL_MATCH_GLASS, String.valueOf(dispensingAreaSettings.isMatchGlass()));
                if (loadCell.getReferenceForceValue() != null) {
                    optionsRepository.setOption(REPO_KEY_LOAD_CELL_REFERENCE_VALUE, String.valueOf(loadCell.getReferenceForceValue()));
                }
                if (loadCell.getReferenceForceValueWeight() != null) {
                    optionsRepository.setOption(REPO_KEY_LOAD_CELL_REFERENCE_WEIGHT, String.valueOf(loadCell.getReferenceForceValueWeight()));
                }
            }
            this.dispensingAreaSettings = null;
            checkedIfLoadCellPersisted = false;
            reloadLoadCell();
            pumpService.broadCastPumpLayout();
        } finally {
            lockService.releaseGlobal(this);
        }
    }

    private void reloadLoadCell() {
        boolean enabled = Boolean.parseBoolean(optionsRepository.getOption(REPO_KEY_LOAD_CELL_ENABLED).orElse(null));

        if (!enabled) {
            if (loadCell != null) {
                loadCell.shutdown();
                loadCell = null;
            }
            LoadCellSettingsDto.Duplex.DispensingArea settings = new LoadCellSettingsDto.Duplex.DispensingArea();
            settings.setMatchGlass(false);
            settings.setCheckGlassPlaced(false);
            this.dispensingAreaSettings = settings;
            return;
        }
        LoadCell loadCell = new LoadCell();
        loadCell.setClkHwPin(optionsRepository.getPinOption(REPO_KEY_LOAD_CELL_CLK_PIN).orElse(null));
        loadCell.setDtHwPin(optionsRepository.getPinOption(REPO_KEY_LOAD_CELL_DT_PIN).orElse(null));
        optionsRepository.getOption(REPO_KEY_LOAD_CELL_ZERO_VALUE)
                .ifPresent(s -> loadCell.setZeroForceValue(Long.parseLong(s)));
        optionsRepository.getOption(REPO_KEY_LOAD_CELL_REFERENCE_VALUE)
                .ifPresent(s -> loadCell.setReferenceForceValue(Long.parseLong(s)));
        optionsRepository.getOption(REPO_KEY_LOAD_CELL_REFERENCE_WEIGHT)
                .ifPresent(s -> loadCell.setReferenceForceValueWeight(Long.parseLong(s)));
        this.loadCell = loadCell;
        LoadCellSettingsDto.Duplex.DispensingArea settings = new LoadCellSettingsDto.Duplex.DispensingArea();
        settings.setMatchGlass(Boolean.parseBoolean(
                optionsRepository.getOption(REPO_KEY_LOAD_CELL_MATCH_GLASS).orElse(String.valueOf(false))
        ));
        settings.setCheckGlassPlaced(Boolean.parseBoolean(
                optionsRepository.getOption(REPO_KEY_LOAD_CELL_CHECK_GLASS_PLACED).orElse(String.valueOf(false))
        ));
        this.dispensingAreaSettings = settings;
    }

    public LoadCell getLoadCell() {
        if (loadCell == null && !checkedIfLoadCellPersisted) {
            reloadLoadCell();
            checkedIfLoadCellPersisted = true;
        }
        return loadCell;
    }

    public LoadCell fromDto(LoadCellSettingsDto.Request.Create dto) {
        if (dto == null) {
            return null;
        }
        LoadCell loadCell = new LoadCell();
        loadCell.setDtHwPin(gpioService.fromDto(dto.getDtPin()));
        loadCell.setClkHwPin(gpioService.fromDto(dto.getClkPin()));
        return loadCell;
    }
}
