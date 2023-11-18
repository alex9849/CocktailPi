package net.alex9849.cocktailpi.payload.dto.eventaction;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import net.alex9849.cocktailpi.model.eventaction.*;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class EventActionDto {
    private interface Id { long getId(); }
    private interface Trigger { @NotNull EventTrigger getTrigger(); }
    private interface ExecutionGroups { @NotNull Set<@NotNull @Size(max = 40) String> getExecutionGroups(); }
    private interface Description { String getDescription(); }
    private interface Comment { @Size(max = 40) String getComment(); }
    private interface Type { String getType(); }

    private interface EventActionId { long getEventActionId(); }
    private interface RunId { long getRunId(); }
    private interface HasLog { boolean isHasLog(); }
    private interface Status { EventActionInformation.Status getStatus(); }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = CallUrlEventActionDto.Request.Create.class, name = "callUrl"),
                @JsonSubTypes.Type(value = ExecutePythonEventActionDto.Request.Create.class, name = "execPy"),
                @JsonSubTypes.Type(value = PlayAudioEventActionDto.Request.Create.class, name = "playAudio"),
                @JsonSubTypes.Type(value = DoNothingEventActionDto.Request.Create.class, name = "doNothing")
        })
        @Getter @Setter @EqualsAndHashCode
        public static class Create implements Trigger, ExecutionGroups, Comment {
            EventTrigger trigger;
            Set<String> executionGroups;
            String comment;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public abstract static class Detailed implements Id, Trigger, ExecutionGroups, Description, Comment, Type {
            long id;
            EventTrigger trigger;
            Set<String> executionGroups;
            String description;
            String comment;

            protected Detailed(EventAction eventAction) {
                BeanUtils.copyProperties(eventAction, this);
            }

            public static EventActionDto.Response.Detailed toDto(EventAction eventAction) {
                if(eventAction == null) {
                    return null;
                }
                if(eventAction instanceof CallUrlEventAction) {
                    return new CallUrlEventActionDto.Response.Detailed((CallUrlEventAction) eventAction);
                }
                if(eventAction instanceof ExecutePythonEventAction) {
                    return new ExecutePythonEventActionDto.Response.Detailed((ExecutePythonEventAction) eventAction);
                }
                if(eventAction instanceof PlayAudioEventAction) {
                    return new PlayAudioEventActionDto.Response.Detailed((PlayAudioEventAction) eventAction);
                }
                if(eventAction instanceof DoNothingEventAction) {
                    return new DoNothingEventActionDto.Response.Detailed((DoNothingEventAction) eventAction);
                }
                throw new IllegalStateException("EventAction-Type is not supported yet!");
            }
        }

        @Getter @Setter @EqualsAndHashCode
        public static class RunInformation implements EventActionId, RunId, HasLog, Status {
            long eventActionId;
            long runId;
            boolean hasLog;
            EventActionInformation.Status status;

            public RunInformation(EventActionInformation eai) {
                BeanUtils.copyProperties(eai, this);
            }
        }
    }
}
