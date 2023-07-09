package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.service.pumps.PumpDataService;
import net.alex9849.cocktailmaker.service.pumps.PumpLockService;
import net.alex9849.cocktailmaker.service.pumps.PumpUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PumpService {

    @Autowired
    private PumpUpService maintenanceService;

    @Autowired
    private PumpLockService lockService;

    @Autowired
    private PumpDataService dataService;

    public void postConstruct() {
        maintenanceService.stopAllPumps();
    }

}
