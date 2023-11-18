package net.alex9849.cocktailpi.payload.dto.system.settings;

import lombok.*;
import net.alex9849.cocktailpi.model.system.settings.I2CSettings;
import net.alex9849.cocktailpi.payload.dto.gpio.PinDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class I2cSettingsDto {

    @Getter @Setter @EqualsAndHashCode
    public static class Request {
        boolean enable;
        PinDto.Request.Select sdaPin;
        PinDto.Request.Select sclPin;
    }

    @Getter @Setter @EqualsAndHashCode
    public static class Response {
        boolean enable;
        PinDto.Response.Detailed sdaPin;
        PinDto.Response.Detailed sclPin;

        public Response(I2CSettings settings) {
            this.enable = settings.isEnable();
            if(settings.getSdaPin() != null) {
                this.sdaPin = new PinDto.Response.Detailed(settings.getSdaPin());
            }
            if(settings.getSclPin() != null) {
                this.sclPin = new PinDto.Response.Detailed(settings.getSclPin());
            }
        }

    }
}
