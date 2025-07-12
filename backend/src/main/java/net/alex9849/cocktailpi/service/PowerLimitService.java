package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.payload.dto.system.settings.PowerLimitSettingsDto;
import net.alex9849.cocktailpi.repository.OptionsRepository;
import net.alex9849.cocktailpi.service.pumps.PumpDataService;
import net.alex9849.cocktailpi.service.pumps.PumpLockService;
import net.alex9849.cocktailpi.service.pumps.PumpTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.OptionalInt;

@Service
public class PowerLimitService {
    @Autowired
    private OptionsRepository optionsRepository;
    @Autowired
    private PumpLockService pumpLockService;

    private final PumpTaskExecutor pumpTaskExecutor = PumpTaskExecutor.getInstance();
    @Autowired
    private PumpDataService pumpDataService;

    public void applyPowerLimit() {
        PowerLimitSettingsDto.Duplex.Detailed limit = getPowerLimit();
        if (limit.isEnable()) {
            pumpTaskExecutor.setPowerLimit(limit.getLimit());
        } else {
            pumpTaskExecutor.setPowerLimit(null);
        }
    }


    public PowerLimitSettingsDto.Duplex.Detailed getPowerLimit() {
        boolean isEnable = Boolean.parseBoolean(optionsRepository.getOption("POWER_LIMIT_ENABLE").orElse(String.valueOf(false)));
        PowerLimitSettingsDto.Duplex.Detailed detailed = new PowerLimitSettingsDto.Duplex.Detailed();
        detailed.setEnable(isEnable);
        if (isEnable) {
            int limit =  Integer.parseInt(optionsRepository.getOption("POWER_LIMIT_LIMIT").get());
            detailed.setLimit(limit);
        }
        return detailed;
    }

    public PowerLimitSettingsDto.Duplex.Detailed setPowerLimit(PowerLimitSettingsDto.Duplex.Detailed detailed) {
        try {
            pumpLockService.acquireGlobal(this);
            if(detailed == null || !detailed.isEnable()) {
                optionsRepository.setOption("POWER_LIMIT_ENABLE", String.valueOf(false));
                optionsRepository.delOption("POWER_LIMIT_LIMIT", false);
            } else {
                OptionalInt maxLimit = pumpDataService.getAllPumps().stream().mapToInt(Pump::getPowerConsumption).max();
                if(maxLimit.isPresent() && maxLimit.getAsInt() > detailed.getLimit()) {
                    throw new IllegalArgumentException("Pump with higher power consumption found! (Minimum " + maxLimit.getAsInt() + "mW)");
                }
                optionsRepository.setOption("POWER_LIMIT_ENABLE", String.valueOf(true));
                optionsRepository.setOption("POWER_LIMIT_LIMIT", String.valueOf(detailed.getLimit()));
            }
        } finally {
            pumpLockService.releaseGlobal(this);
        }
        PowerLimitSettingsDto.Duplex.Detailed limit = getPowerLimit();
        if (limit.isEnable()) {
            pumpTaskExecutor.setPowerLimit(limit.getLimit());
        } else {
            pumpTaskExecutor.setPowerLimit(null);
        }
        return limit;
    }

}
