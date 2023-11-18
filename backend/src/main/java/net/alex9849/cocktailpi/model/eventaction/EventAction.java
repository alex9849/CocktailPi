package net.alex9849.cocktailpi.model.eventaction;

import java.util.Set;

public abstract class EventAction {
    private long id;
    private EventTrigger trigger;
    private Set<String> executionGroups;
    private String comment;

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

    /**
     * Actions that are in the same ExecutionGroup can run concurrently.
     *
     * If an Action is started that belongs to another ExecutionGroup, all Actions that have
     * an ExecutionGroup and don't belong to it's group, will be cancelled
     *
     * If an action doesn't belong to an ExecutionGroup it will never be cancelled by
     * another action and won't cancel other running actions.
     *
     * @return The ExecutionGroup
     */
    public Set<String> getExecutionGroups() {
        return executionGroups;
    }

    public void setExecutionGroups(Set<String> executionGroups) {
        this.executionGroups = executionGroups;
    }

    /**
     * This methods contains the behaviour of the EventAction.
     * The trigger always gets executed asynchronous.
     */
    public abstract void trigger(RunningAction runningAction);

    public abstract String getDescription();

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
