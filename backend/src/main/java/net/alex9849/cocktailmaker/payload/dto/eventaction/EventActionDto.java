package net.alex9849.cocktailmaker.payload.dto.eventaction;

import net.alex9849.cocktailmaker.model.eventaction.EventAction;
import net.alex9849.cocktailmaker.model.eventaction.EventTrigger;

import java.util.Set;

public abstract class EventActionDto {
    private long id;
    private EventTrigger trigger;
    private Set<String> executionGroups;

    public static EventActionDto toDto(EventAction eventAction) {
        //Todo
        return null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EventTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(EventTrigger trigger) {
        this.trigger = trigger;
    }

    public Set<String> getExecutionGroups() {
        return executionGroups;
    }

    public void setExecutionGroups(Set<String> executionGroups) {
        this.executionGroups = executionGroups;
    }
}
