package net.alex9849.cocktailpi.model.eventaction;

public class EventActionInformation {
    private long eventActionId;
    private long runId;
    private boolean hasLog;
    private Status status;

    public long getEventActionId() {
        return eventActionId;
    }

    public void setEventActionId(long eventActionId) {
        this.eventActionId = eventActionId;
    }

    public long getRunId() {
        return runId;
    }

    public void setRunId(long runId) {
        this.runId = runId;
    }

    public boolean isHasLog() {
        return hasLog;
    }

    public void setHasLog(boolean hasLog) {
        this.hasLog = hasLog;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        RUNNING, ERROR, STOPPED
    }
}
