package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.system.settings.ReversePumpSettings;
import net.alex9849.cocktailpi.payload.dto.system.settings.ReversePumpSettingsDto;
import net.alex9849.cocktailpi.service.pumps.PumpLockService;
import net.alex9849.cocktailpi.service.pumps.PumpMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReversePumpSettingsService {

    @Autowired
    private PumpMaintenanceService maintenanceService;

    @Autowired
    private PumpLockService lockService;



    public void setReversePumpingSettings(ReversePumpSettings settings) {
        if (!lockService.testAndAcquireGlobal(maintenanceService)) {
            throw new IllegalArgumentException("Some pumps are currently occupied!");
        }
        try {
            maintenanceService.setReversePumpingSettings(settings);
        } finally {
            lockService.releaseGlobal(maintenanceService);
        }
    }

    public ReversePumpSettings fromDto(ReversePumpSettingsDto.Request.Create dto) {
        return maintenanceService.fromDto(dto);
    }


    public ReversePumpSettings getReversePumpingSettings() {
        return maintenanceService.getReversePumpingSettings();
    }

}
