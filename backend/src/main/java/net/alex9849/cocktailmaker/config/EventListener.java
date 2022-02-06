package net.alex9849.cocktailmaker.config;

import net.alex9849.cocktailmaker.model.eventaction.EventTrigger;
import net.alex9849.cocktailmaker.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

@Component
public class EventListener {
    @Autowired
    private EventService eventService;

    @org.springframework.context.event.EventListener
    public void handleContextStart(ContextStartedEvent cse) {
        eventService.triggerActions(EventTrigger.COCKTAIL_PRODUCTION_STARTED);
    }
}
