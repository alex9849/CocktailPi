package net.alex9849.cocktailmaker.payload.dto.gpio;

import lombok.*;
import net.alex9849.cocktailmaker.model.gpio.GpioBoard;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class I2CGpioBoardDto {
    private interface I2cAddress { byte getAddress(); }
    private interface BoardModel { String getBoardModel(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Create extends GpioBoardDto.Request.Create implements I2cAddress, BoardModel {
            String boardModel;
            byte address;

        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends GpioBoardDto.Response.Detailed implements I2cAddress, BoardModel {
            String boardModel;
            byte address;

            protected Detailed(GpioBoard gpioBoard) {
                super(gpioBoard);
            }

            @Override
            public String getType() {
                return "i2c";
            }
        }

    }

}
