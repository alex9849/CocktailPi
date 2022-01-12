package net.alex9849.cocktailmaker.model.eventaction;

public abstract class EventAction {
    private long id;
    private EventTrigger trigger;

    protected EventAction(long id, EventTrigger trigger) {
        this.id = id;
        this.trigger = trigger;
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
}
