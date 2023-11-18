package net.alex9849.cocktailpi.payload.dto.eventaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import net.alex9849.cocktailpi.model.eventaction.CallUrlEventAction;
import org.springframework.web.bind.annotation.RequestMethod;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CallUrlEventActionDto {
    private interface RequestCommand { @NotNull RequestMethod getRequestMethod(); }
    private interface Url { @NotNull @Size(max = 255) String getUrl(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Create extends EventActionDto.Request.Create implements RequestCommand, Url {
            RequestMethod requestMethod;
            String url;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends EventActionDto.Response.Detailed implements RequestCommand, Url {
            RequestMethod requestMethod;
            String url;

            protected Detailed(CallUrlEventAction eventAction) {
                super(eventAction);
            }

            public String getType() {
                return "callUrl";
            }
        }
    }
}
