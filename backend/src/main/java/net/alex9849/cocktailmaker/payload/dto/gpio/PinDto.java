package net.alex9849.cocktailmaker.payload.dto.gpio;

import lombok.*;
import net.alex9849.cocktailmaker.model.gpio.GpioBoard;

public class PinDto {
    private interface Nr { int getNr(); }
    private interface PinResource { PinResourceDto.Response.Detailed getPinResource(); }
    private interface InUse { boolean isInUse(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode
        public static class Detailed implements Nr, PinResource, InUse {
            int nr;
            PinResourceDto.Response.Detailed pinResource;
            boolean inUse;

            public Detailed(GpioBoard.Pin pin) {
                this.nr = pin.getPinNr();
                if(pin.getResource() != null) {
                    this.pinResource = new PinResourceDto.Response.Detailed(pin.getResource());
                }
                this.inUse = this.pinResource != null;
            }
        }

    }

}
