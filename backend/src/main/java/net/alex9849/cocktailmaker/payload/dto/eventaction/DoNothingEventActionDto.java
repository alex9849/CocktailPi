package net.alex9849.cocktailmaker.payload.dto.eventaction;

import lombok.*;
import net.alex9849.cocktailmaker.model.eventaction.DoNothingEventAction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DoNothingEventActionDto {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Create extends EventActionDto.Request.Create {
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends EventActionDto.Response.Detailed {

            protected Detailed(DoNothingEventAction eventAction) {
                super(eventAction);
            }

            public String getType() {
                return "doNothing";
            }
        }
    }
}
