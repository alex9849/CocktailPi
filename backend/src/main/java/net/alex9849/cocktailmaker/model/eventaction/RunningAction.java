package net.alex9849.cocktailmaker.model.eventaction;

import java.util.concurrent.Future;

public class RunningAction {
    private static long nextRunId;
    private long runId;
    private Future<?> future;
    private EventAction eventAction;

    public RunningAction(EventAction eventAction) {
        this.runId = nextRunId++;
        this.eventAction = eventAction;
        this.future = future;
    }

    public Future<?> getFuture() {
        return future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public EventAction getEventAction() {
        return eventAction;
    }

    public long getRunId() {
        return runId;
    }
}
