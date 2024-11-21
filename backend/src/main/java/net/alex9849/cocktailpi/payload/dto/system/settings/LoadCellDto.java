package net.alex9849.cocktailpi.payload.dto.system.settings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.alex9849.cocktailpi.model.LoadCell;
import net.alex9849.cocktailpi.payload.dto.gpio.PinDto;

public class LoadCellDto {

    private interface IClkPin { @NotNull @Valid PinDto.Request.Select getClkPin(); }
    private interface IDtPin { @NotNull @Valid PinDto.Request.Select getDtPin(); }
    private interface IClkPinResponse { PinDto.Response.Detailed getClkPin(); }
    private interface IDtPinResponse { PinDto.Response.Detailed getClkPin(); }
    private interface ICalibrated { boolean isCalibrated(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter @Setter @EqualsAndHashCode
        public static class Create implements IClkPin, IDtPin {
            PinDto.Request.Select clkPin;
            PinDto.Request.Select dtPin;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements IClkPinResponse, IDtPinResponse, ICalibrated {
            PinDto.Response.Detailed clkPin;
            PinDto.Response.Detailed dtPin;
            boolean calibrated;

            public Detailed(LoadCell loadCell) {
                clkPin = new PinDto.Response.Detailed(loadCell.getClkPin());
                clkPin = new PinDto.Response.Detailed(loadCell.getDtPin());
                calibrated = loadCell.isCalibrated();
            }

        }

    }
}
