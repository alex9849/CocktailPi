package net.alex9849.cocktailpi.repository;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.gpio.PinResource;
import net.alex9849.cocktailpi.model.gpio.i2cboard.I2CBoardModel;
import net.alex9849.cocktailpi.model.gpio.i2cboard.I2CGpioBoard;
import net.alex9849.cocktailpi.model.gpio.local.LocalGpioBoard;
import net.alex9849.cocktailpi.model.system.GpioStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GpioRepositoryTest extends BackendIntegrationTestBase {

    @Autowired
    private GpioRepository gpioRepository;

    @Test
    void listBoardsAndStatus() {
        List<GpioBoard> boards = gpioRepository.getBoards();
        assertTrue(boards.size() > 0);

        GpioStatus status = gpioRepository.getGpioStatus();
        assertNotNull(status);
    }

    @Test
    void createAndFindI2cBoard() {
        I2CGpioBoard board = I2CBoardModel.genInstance(I2CBoardModel.MCP23017);
        board.setName("RepoI2C");
        board.setI2cAddress((byte) 0x21);

        GpioBoard created = gpioRepository.createBoard(board);
        assertNotNull(created);

        Optional<PinResource> pr = gpioRepository.getPinResourceByI2CAddress(0x21);
        assertTrue(pr.isPresent());
    }

    @Test
    void createAndUpdateLocalBoard() {
        LocalGpioBoard board = new LocalGpioBoard();
        board.setName("RepoLocal");
        GpioBoard created = gpioRepository.createBoard(board);
        created.setName("RepoLocalUpdated");
        assertTrue(gpioRepository.updateBoard(created));
    }
}
