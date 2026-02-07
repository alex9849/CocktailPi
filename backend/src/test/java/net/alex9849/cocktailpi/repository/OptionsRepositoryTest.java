package net.alex9849.cocktailpi.repository;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.gpio.HardwarePin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionsRepositoryTest extends BackendIntegrationTestBase {

    @Autowired
    private OptionsRepository optionsRepository;
    @Autowired
    private GpioRepository gpioRepository;

    @Test
    void setAndGetOption() {
        optionsRepository.setOption("TEST_KEY", "value");
        assertEquals("value", optionsRepository.getOption("TEST_KEY").orElse(null));
        optionsRepository.delOption("TEST_KEY", false);
    }

    @Test
    void setAndGetPinOption() {
        GpioBoard board = gpioRepository.getBoards().get(0);
        HardwarePin pin = board.getPin(board.getMinPin());
        optionsRepository.setPinOption("TEST_PIN", pin);
        HardwarePin loaded = optionsRepository.getPinOption("TEST_PIN").orElse(null);
        assertTrue(loaded != null && loaded.getPinNr() == pin.getPinNr());
        optionsRepository.delOption("TEST_PIN", false);
    }
}
