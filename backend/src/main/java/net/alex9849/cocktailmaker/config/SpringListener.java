package net.alex9849.cocktailmaker.config;

import net.alex9849.cocktailmaker.model.eventaction.EventTrigger;
import net.alex9849.cocktailmaker.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Lazy(false)
@Component
public class SpringListener {
    @Autowired
    private EventService eventService;

    @EventListener
    public void handleContextStart(ApplicationReadyEvent event) {
        eventService.triggerActions(EventTrigger.APPLICATION_STARTUP);
    }
}
