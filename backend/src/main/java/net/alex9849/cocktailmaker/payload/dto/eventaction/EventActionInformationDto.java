package net.alex9849.cocktailmaker.payload.dto.eventaction;

import net.alex9849.cocktailmaker.model.eventaction.EventActionInformation;
import org.springframework.beans.BeanUtils;

public class EventActionInformationDto {
    private long eventActionId;
    private long runId;
    private boolean hasLog;
    private EventActionInformation.Status status;

    public EventActionInformationDto(EventActionInformation eai) {
        BeanUtils.copyProperties(eai, this);
    }

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

    public EventActionInformation.Status getStatus() {
        return status;
    }

    public void setStatus(EventActionInformation.Status status) {
        this.status = status;
    }
}
