package net.alex9849.cocktailpi.payload.dto.pump;

import jakarta.validation.constraints.Min;
import lombok.*;
import net.alex9849.cocktailpi.model.pump.DcPump;
import net.alex9849.cocktailpi.payload.dto.gpio.PinDto;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DcPumpDto {
    private interface TimePerClInMs { @Min(1) Integer getTimePerClInMs(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        @NoArgsConstructor(access = AccessLevel.PUBLIC)
        public static class Create extends OnOffPumpDto.Request.Create implements TimePerClInMs {
            Integer timePerClInMs;

            public Create(DcPump pump) {
                super(pump);
                this.timePerClInMs = pump.getTimePerClInMs();
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends OnOffPumpDto.Response.Detailed implements TimePerClInMs {
            Integer timePerClInMs;

            protected Detailed(DcPump dcPump) {
                super(dcPump);
                timePerClInMs = dcPump.getTimePerClInMs();
            }

            @Override
            public String getType() {
                return "dc";
            }
        }
    }
}
