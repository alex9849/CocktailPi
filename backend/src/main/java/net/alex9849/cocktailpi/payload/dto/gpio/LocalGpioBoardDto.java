package net.alex9849.cocktailpi.payload.dto.gpio;

import lombok.*;
import net.alex9849.cocktailpi.model.gpio.GpioBoard;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalGpioBoardDto {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Create extends GpioBoardDto.Request.Create {
        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends GpioBoardDto.Response.Detailed {

            protected Detailed(GpioBoard gpioBoard) {
                super(gpioBoard);
            }

            @Override
            public String getType() {
                return "local";
            }
        }

    }

}
