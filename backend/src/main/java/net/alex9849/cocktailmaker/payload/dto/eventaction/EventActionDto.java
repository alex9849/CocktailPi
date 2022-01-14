package net.alex9849.cocktailmaker.payload.dto.eventaction;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.alex9849.cocktailmaker.model.eventaction.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CallUrlEventActionDto.class, name = "callUrl"),
        @JsonSubTypes.Type(value = ExecutePythonEventActionDto.class, name = "execPy"),
        @JsonSubTypes.Type(value = PlayAudioEventActionDto.class, name = "playAudio")
})
public abstract class EventActionDto {
    private long id;
    @NotNull
    private EventTrigger trigger;
    @NotNull
    private Set<@NotNull @Size(max = 40) String> executionGroups;
    private String description;
    @Size(max = 40)
    private String comment;

    public static EventActionDto toDto(EventAction eventAction) {
        if(eventAction instanceof CallUrlEventAction) {
            return new CallUrlEventActionDto((CallUrlEventAction) eventAction);
        }
        if(eventAction instanceof ExecutePythonEventAction) {
            return new ExecutePythonEventActionDto((ExecutePythonEventAction) eventAction);
        }
        if(eventAction instanceof PlayAudioEventAction) {
            return new PlayAudioEventActionDto((PlayAudioEventAction) eventAction);
        }
        throw new IllegalStateException("EventAction-Type is not supported yet!");
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public abstract String getType();
}
