package net.alex9849.cocktailmaker.config;

import jakarta.annotation.PostConstruct;
import net.alex9849.cocktailmaker.config.seed.SeedDataInserter;
import net.alex9849.cocktailmaker.model.eventaction.EventTrigger;
import net.alex9849.cocktailmaker.service.EventService;
import net.alex9849.cocktailmaker.service.PumpService;
import net.alex9849.cocktailmaker.service.pumps.PumpMaintenanceService;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Lazy(false)
@Component
public class SpringListener {
    @Autowired
    private EventService eventService;

    @Autowired
    private PumpService pumpService;

    @Autowired
    private PumpMaintenanceService pumpUpService;

    @Autowired
    private Flyway flyway;

    @Autowired
    private SeedDataInserter seedDataInserter;

    @PostConstruct
    public void onPostConstruct() throws Exception {
        flyway.migrate();
        seedDataInserter.migrate();
        pumpService.postConstruct();
        pumpUpService.postConstruct();
    }

    @EventListener
    public void handleApplicationReady(ApplicationReadyEvent event) {
        eventService.triggerActions(EventTrigger.APPLICATION_STARTUP);
    }
}
