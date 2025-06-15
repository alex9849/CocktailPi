package net.alex9849.cocktailpi.payload.dto.system.settings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.alex9849.cocktailpi.model.system.settings.ReversePumpSettings;
import net.alex9849.cocktailpi.payload.dto.gpio.PinDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReversePumpSettingsDto {
    private interface IEnable { boolean isEnable(); }
    private interface ISettingsRequest { @Valid Config.Request.Create getSettings(); }
    private interface ISettingsResponse { @Valid Config.Response.Detailed getSettings(); }
    private interface IDirectorPinResponse { @NotNull @Valid PinDto.Response.Detailed getDirectorPin(); }
    private interface IDirectorPinRequest { @NotNull PinDto.Request.Select getDirectorPin(); }
    private interface IOvershoot { @Min(0) @Max(200) int getOvershoot(); }
    private interface IAutoPumpBackTimer { @Min(0) @Max(60) int getAutoPumpBackTimer(); }
    private interface IForwardStateHigh { boolean isForwardStateHigh(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter @Setter @EqualsAndHashCode
        public static class Create implements IEnable, ISettingsRequest {
            boolean enable;
            Config.Request.Create settings;
        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements IEnable, ISettingsResponse {
            boolean enable;
            Config.Response.Detailed settings;

            public Detailed(ReversePumpSettings rps) {
                this.enable = rps.isEnable();
                if(rps.getSettings() != null) {
                    this.settings = new Config.Response.Detailed(rps.getSettings());
                }
            }

        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Config {

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Request {

            @Getter @Setter @EqualsAndHashCode
            public static class Create implements IDirectorPinRequest, IOvershoot, IForwardStateHigh, IAutoPumpBackTimer {
                PinDto.Request.Select directorPin;
                int overshoot;
                int autoPumpBackTimer;
                boolean forwardStateHigh;
            }

        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Response {
            @Getter @Setter @EqualsAndHashCode
            public static class Detailed implements IDirectorPinResponse, IOvershoot, IForwardStateHigh, IAutoPumpBackTimer {
                PinDto.Response.Detailed directorPin;
                int overshoot;
                int autoPumpBackTimer;
                boolean forwardStateHigh;

                public Detailed(ReversePumpSettings.Config cfg) {
                    this.overshoot = cfg.getOvershoot();
                    this.autoPumpBackTimer = cfg.getAutoPumpBackTimer();
                    this.forwardStateHigh = cfg.isForwardStateHigh();
                    if(cfg.getDirectorPin() != null) {
                        this.directorPin = new PinDto.Response.Detailed(cfg.getDirectorPin());
                    }
                }
            }

        }

    }
}
