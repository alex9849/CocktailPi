package net.alex9849.cocktailpi.payload.dto.pump;

import lombok.*;
import net.alex9849.cocktailpi.model.pump.OnOffPump;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.payload.dto.gpio.PinDto;

public class OnOffPumpDto {

    private interface PinResponse { PinDto.Response.PumpPin getPin(); }
    private interface PinRequest { PinDto.Request.Select getPin(); }
    private interface IsPowerStateHigh { Boolean getIsPowerStateHigh(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        @NoArgsConstructor(access = AccessLevel.PUBLIC)
        public static class Create extends PumpDto.Request.Create implements PinRequest, IsPowerStateHigh {
            PinDto.Request.Select pin;
            Boolean isPowerStateHigh;

            public Create(OnOffPump pump) {
                super(pump);
                if (pump.getPin() != null) {
                    this.pin  = new PinDto.Request.Select(pump.getPin());
                }
                this.isPowerStateHigh = pump.isPowerStateHigh();
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public static abstract class Detailed extends PumpDto.Response.Detailed implements PinResponse, IsPowerStateHigh {
            PinDto.Response.PumpPin pin;
            Boolean isPowerStateHigh;


            protected Detailed(OnOffPump onOffPump) {
                super(onOffPump);
                this.isPowerStateHigh = onOffPump.isPowerStateHigh();
                if(onOffPump.getPin() != null) {
                    pin = new PinDto.Response.PumpPin(onOffPump.getPin());
                }
            }
        }
    }

}
