package net.alex9849.cocktailpi.payload.dto.gpio;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.alex9849.cocktailpi.model.gpio.HardwarePin;

public class PinDto {
    private interface Nr { @NotNull int getNr(); }
    private interface BoardId { @NotNull long getBoardId(); }
    private interface BoardName { String getBoardName(); }
    private interface PinName { String getPinName(); }
    private interface PinResource { PinResourceDto.Response.Detailed getPinResource(); }
    private interface InUse { boolean isInUse(); }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter @Setter
        @EqualsAndHashCode
        @NoArgsConstructor(access = AccessLevel.PUBLIC)
        public static class Select implements Nr, BoardId {
            int nr;
            long boardId;

            public Select(HardwarePin pin) {
                this.nr = pin.getPinNr();
                this.boardId = pin.getBoardId();
            }
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

            public PumpPin(HardwarePin hwPin) {
                this.nr = hwPin.getPinNr();
                this.boardId = hwPin.getBoardId();
                this.inUse = hwPin.getResource() != null;
                this.boardName = hwPin.getGpioBoard().getName();
                this.pinName = hwPin.getDisplayName();
            }
        }

        @Getter
        @Setter
        @EqualsAndHashCode
        public static class Detailed implements Nr, BoardId, InUse, PinName, BoardName, PinResource {
            int nr;
            long boardId;
            boolean inUse;
            String pinName;
            String boardName;
            PinResourceDto.Response.Detailed pinResource;

            public Detailed(HardwarePin hwPin) {
                this.nr = hwPin.getPinNr();
                this.boardId = hwPin.getBoardId();
                this.inUse = hwPin.getResource() != null;
                this.pinName = hwPin.getDisplayName();
                this.boardName = hwPin.getGpioBoard().getName();
                if(hwPin.getResource() != null) {
                    this.pinResource = new PinResourceDto.Response.Detailed(hwPin.getResource());
                }
            }
        }

    }

}
