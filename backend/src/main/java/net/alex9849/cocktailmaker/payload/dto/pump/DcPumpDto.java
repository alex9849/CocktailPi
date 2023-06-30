package net.alex9849.cocktailmaker.payload.dto.pump;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DcPumpDto {
    private interface Pin { @Min(0) @Max(31) Integer getPin(); }
    private interface TimePerClInMs { @Min(1) Integer getTimePerClInMs(); }
    private interface IsPowerStateHigh { Boolean getIsPowerStateHigh(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Patch extends PumpDto.Request.Patch implements Pin, TimePerClInMs, IsPowerStateHigh {
            Integer pin;
            Integer timePerClInMs;
            Boolean isPowerStateHigh;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public abstract static class Detailed extends PumpDto.Response.Detailed implements Pin, TimePerClInMs, IsPowerStateHigh {
            Integer pin;
            Integer timePerClInMs;
            Boolean isPowerStateHigh;

            protected Detailed(DcPump dcPump) {
                super(dcPump);
            }
        }
    }
}
}
