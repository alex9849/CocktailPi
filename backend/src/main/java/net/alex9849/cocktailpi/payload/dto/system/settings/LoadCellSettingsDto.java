package net.alex9849.cocktailpi.payload.dto.system.settings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.alex9849.cocktailpi.model.LoadCell;
import net.alex9849.cocktailpi.payload.dto.gpio.PinDto;

public class LoadCellSettingsDto {

    private interface IClkPin { @NotNull @Valid PinDto.Request.Select getClkPin(); }
    private interface IDtPin { @NotNull @Valid PinDto.Request.Select getDtPin(); }
    private interface IClkPinResponse { PinDto.Response.Detailed getClkPin(); }
    private interface IDtPinResponse { PinDto.Response.Detailed getClkPin(); }
    private interface ICalibrated { boolean isCalibrated(); }
    private interface ICheckGlassPlaced { boolean isCheckGlassPlaced(); }
    private interface IMatchGlass { boolean isMatchGlass(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter @Setter @EqualsAndHashCode
        public static class Create implements IClkPin, IDtPin {
            PinDto.Request.Select clkPin;
            PinDto.Request.Select dtPin;
            Duplex.DispensingArea dispensingArea;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements IClkPinResponse, IDtPinResponse, ICalibrated {
            PinDto.Response.Detailed clkPin;
            PinDto.Response.Detailed dtPin;
            Duplex.DispensingArea dispensingArea;
            boolean calibrated;

            public Detailed(LoadCell loadCell, Duplex.DispensingArea dispensingAreaSettings) {
                clkPin = new PinDto.Response.Detailed(loadCell.getClkHwPin());
                dtPin = new PinDto.Response.Detailed(loadCell.getDtHwPin());
                calibrated = loadCell.isCalibrated();
                dispensingArea = dispensingAreaSettings;
            }

        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Duplex {

        @Getter @Setter @EqualsAndHashCode
        public static class DispensingArea implements ICheckGlassPlaced, IMatchGlass {
            boolean checkGlassPlaced;
            boolean matchGlass;
        }
    }
}
