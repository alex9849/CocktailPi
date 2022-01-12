package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.eventaction.EventAction;
import net.alex9849.cocktailmaker.payload.dto.eventaction.EventActionDto;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EventService {

    public static EventAction fromDto(EventActionDto dto) {
        //Todo
        return null;
    }

    public List<EventAction> getEventActions() {
        //TODO
        return new ArrayList<>();
    }

    public EventAction getEventAction(long id) {
        //TODO
        return null;
    }

    public boolean deleteEventAction(long id) {
        //TODO
        return false;
    }

    public EventAction createEventAction(EventAction eventAction) {
        //TODO
        return null;
    }

    public EventAction updateEventAction(EventAction updatedEventAction) {
        //TODO
        //Check if exists
        return null;
    }
}
