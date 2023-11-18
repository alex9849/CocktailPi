package net.alex9849.cocktailpi.payload.dto.eventaction;

import lombok.*;
import net.alex9849.cocktailpi.model.eventaction.ExecutePythonEventAction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExecutePythonEventActionDto extends FileEventActionDto {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Create extends FileEventActionDto.Request.Create {
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends FileEventActionDto.Response.Detailed {

            protected Detailed(ExecutePythonEventAction eventAction) {
                super(eventAction);
            }

            public String getType() {
                return "execPy";
            }
        }
    }
}
