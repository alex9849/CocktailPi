package net.alex9849.cocktailmaker.payload.dto.gpio;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.alex9849.cocktailmaker.model.gpio.GpioBoard;

public class PinDto {
    private interface Nr { @NotNull int getNr(); }
    private interface BoardId { @NotNull long getBoardId(); }
    private interface PinResource { PinResourceDto.Response.Detailed getPinResource(); }
    private interface InUse { boolean isInUse(); }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter
        @Setter
        @EqualsAndHashCode
        public static class Select implements Nr, BoardId {
            int nr;
            long boardId;
        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode
        public static class Reduced implements Nr, BoardId, InUse {
            int nr;
            long boardId;
            boolean inUse;

            public Reduced(GpioBoard.Pin pin) {
                this.nr = pin.getPinNr();
                this.boardId = pin.getBoardId();
                this.inUse = pin.getResource() != null;
            }
        }

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends Reduced implements PinResource {
            PinResourceDto.Response.Detailed pinResource;

            public Detailed(GpioBoard.Pin pin) {
                super(pin);
                if(pin.getResource() != null) {
                    this.pinResource = new PinResourceDto.Response.Detailed(pin.getResource());
                }
            }
        }

    }

}
