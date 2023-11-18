package net.alex9849.cocktailpi.config;

import net.alex9849.cocktailpi.model.eventaction.EventTrigger;
import net.alex9849.cocktailpi.service.EventService;
import net.alex9849.cocktailpi.service.pumps.PumpMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
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
    private PumpMaintenanceService pumpUpService;

    @EventListener
    public void handleContextRefreshed(ContextRefreshedEvent event) {
        pumpUpService.postConstruct();
    }

    @EventListener
    public void handleApplicationReady(ApplicationReadyEvent event) {
        eventService.triggerActions(EventTrigger.APPLICATION_STARTUP);
    }
}
