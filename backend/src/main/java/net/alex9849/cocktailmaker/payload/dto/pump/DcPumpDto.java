package net.alex9849.cocktailmaker.payload.dto.pump;

import jakarta.validation.constraints.Min;
import lombok.*;
import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.payload.dto.gpio.PinDto;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DcPumpDto {
    private interface PinResponse { PinDto.Response.PumpPin getPin(); }
    private interface PinRequest { PinDto.Request.Select getPin(); }
    private interface TimePerClInMs { @Min(1) Integer getTimePerClInMs(); }
    private interface IsPowerStateHigh { Boolean getIsPowerStateHigh(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Create extends PumpDto.Request.Create implements PinRequest, TimePerClInMs, IsPowerStateHigh {
            PinDto.Request.Select pin;
            Integer timePerClInMs;
            Boolean isPowerStateHigh;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends PumpDto.Response.Detailed implements PinResponse, TimePerClInMs, IsPowerStateHigh {
            PinDto.Response.PumpPin pin;
            Integer timePerClInMs;
            Boolean isPowerStateHigh;


            protected Detailed(DcPump dcPump) {
                super(dcPump);
                this.isPowerStateHigh = dcPump.isPowerStateHigh();
                if(dcPump.getPin() != null) {
                    pin = new PinDto.Response.PumpPin(dcPump.getPin());
                }
            }

            @Override
            public String getType() {
                return "dc";
            }
        }
    }
}
