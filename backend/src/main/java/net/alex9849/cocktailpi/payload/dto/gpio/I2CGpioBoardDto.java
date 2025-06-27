package net.alex9849.cocktailpi.payload.dto.gpio;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.alex9849.cocktailpi.model.gpio.i2cboard.I2CGpioBoard;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class I2CGpioBoardDto {
    private interface I2cAddress { @NotNull byte getAddress(); }
    private interface BoardModel { @NotNull String getBoardModel(); }

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

            protected Detailed(I2CGpioBoard gpioBoard) {
                super(gpioBoard);
                this.boardModel = gpioBoard.getBoardModel().toString();
                this.address = gpioBoard.getI2cAddress();
            }

            @Override
            public String getType() {
                return "i2c";
            }
        }

    }

}
