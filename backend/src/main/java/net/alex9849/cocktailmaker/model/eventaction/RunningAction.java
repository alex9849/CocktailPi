package net.alex9849.cocktailmaker.model.eventaction;

import java.util.concurrent.CompletableFuture;

public class RunningAction {
    private static long nextRunId;
    private long runId;
    private CompletableFuture<?> future;
    private EventAction eventAction;

    public RunningAction(EventAction eventAction, CompletableFuture<?> future) {
        this.runId = nextRunId++;
        this.eventAction = eventAction;
        this.future = future;
    }

    public CompletableFuture<?> getFuture() {
        return future;
    }

    public EventAction getEventAction() {
        return eventAction;
    }

    public long getRunId() {
        return runId;
    }
}
