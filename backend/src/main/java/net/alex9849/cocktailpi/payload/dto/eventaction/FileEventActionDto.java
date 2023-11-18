package net.alex9849.cocktailpi.payload.dto.eventaction;

import jakarta.validation.constraints.Size;
import lombok.*;
import net.alex9849.cocktailpi.model.eventaction.FileEventAction;

public abstract class FileEventActionDto {
    private interface FileName { @Size(max = 255) String getFileName(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public abstract static class Create extends EventActionDto.Request.Create {
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public abstract static class Detailed extends EventActionDto.Response.Detailed implements FileName {
            String fileName;

            protected Detailed(FileEventAction eventAction) {
                super(eventAction);
            }
        }
    }
}
