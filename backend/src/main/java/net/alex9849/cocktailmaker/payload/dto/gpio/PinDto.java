package net.alex9849.cocktailmaker.payload.dto.gpio;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.alex9849.cocktailmaker.model.gpio.GpioBoard;
import net.alex9849.cocktailmaker.model.gpio.LocalGpioBoard;

public class PinDto {
    private interface Nr { @NotNull int getNr(); }
    private interface BoardId { @NotNull long getBoardId(); }
    private interface BoardName { String getBoardName(); }
    private interface PinName { String getPinName(); }
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
        public static class PumpPin implements Nr, BoardId, InUse, PinName, BoardName {
            int nr;
            long boardId;
            String boardName;
            boolean inUse;
            String pinName;

            public PumpPin(GpioBoard.Pin pin) {
                this.nr = pin.getPinNr();
                this.boardId = pin.getBoardId();
                this.inUse = pin.getResource() != null;
                this.boardName = pin.getGpioBoard().getName();
                this.pinName = (pin.getGpioBoard() instanceof LocalGpioBoard ? "BCM " : "GPIO ") + pin.getPinNr();
            }
        }

        @Getter
        @Setter
        @EqualsAndHashCode
        public static class Detailed implements Nr, BoardId, InUse, PinResource {
            int nr;
            long boardId;
            boolean inUse;
            PinResourceDto.Response.Detailed pinResource;

            public Detailed(GpioBoard.Pin pin) {
                this.nr = pin.getPinNr();
                this.boardId = pin.getBoardId();
                this.inUse = pin.getResource() != null;
                if(pin.getResource() != null) {
                    this.pinResource = new PinResourceDto.Response.Detailed(pin.getResource());
                }
            }
        }

    }

}
