package net.alex9849.cocktailpi.utils;

import com.pi4j.io.gpio.digital.InputPin;
import com.pi4j.io.gpio.digital.OutputPin;
import com.pi4j.io.gpio.digital.PullResistance;
import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.gpio.GpioBoardType;
import net.alex9849.cocktailpi.model.gpio.HardwarePin;
import net.alex9849.cocktailpi.model.gpio.PinResource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PinUtilsTest {

    @Test
    void allowsPinWhenResourceMatchesAllowedTypeAndId() {
        TestGpioBoard board = new TestGpioBoard(1L);
        PinResource resource = new PinResource();
        resource.setId(5L);
        resource.setType(PinResource.Type.PUMP);
        HardwarePin pin = new TestHardwarePin(board, 1, resource);

        assertDoesNotThrow(() ->
                PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.PUMP, 5L, pin));
    }

    @Test
    void throwsWhenPinsAreDoubled() {
        TestGpioBoard board = new TestGpioBoard(2L);
        HardwarePin pinA = new TestHardwarePin(board, 1, null);
        HardwarePin pinB = new TestHardwarePin(board, 1, null);

        assertThrows(IllegalArgumentException.class, () ->
                PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.PUMP, null, pinA, pinB));
    }

    @Test
    void throwsWhenPinIsAlreadyInUseWithDifferentType() {
        TestGpioBoard board = new TestGpioBoard(3L);
        PinResource resource = new PinResource();
        resource.setId(8L);
        resource.setType(PinResource.Type.I2C);
        HardwarePin pin = new TestHardwarePin(board, 2, resource);

        assertThrows(IllegalArgumentException.class, () ->
                PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.PUMP, null, pin));
    }

    private static final class TestGpioBoard extends GpioBoard {
        private TestGpioBoard(long id) {
            setId(id);
        }

        @Override
        public int getMinPin() {
            return 1;
        }

        @Override
        public int getMaxPin() {
            return 1;
        }

        @Override
        protected HardwarePin getPinUnchecked(int pin) {
            return new TestHardwarePin(this, pin, null);
        }

        @Override
        protected String pinDisplayName(int pin) {
            return "P" + pin;
        }

        @Override
        public GpioBoardType getType() {
            return GpioBoardType.LOCAL;
        }
    }

    private static final class TestHardwarePin extends HardwarePin {
        private final PinResource resource;

        private TestHardwarePin(GpioBoard board, int nr, PinResource resource) {
            super(board, nr, "P" + nr);
            this.resource = resource;
        }

        @Override
        public PinResource getResource() {
            return resource;
        }

        @Override
        public OutputPin getOutputPin() {
            return null;
        }

        @Override
        public InputPin getInputPin(PullResistance pull) {
            return null;
        }
    }
}
