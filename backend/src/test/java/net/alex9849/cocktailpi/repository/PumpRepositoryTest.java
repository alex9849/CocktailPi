package net.alex9849.cocktailpi.repository;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.pump.DcPump;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.pump.StepperPump;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PumpRepositoryTest extends BackendIntegrationTestBase {

    @Autowired
    private PumpRepository pumpRepository;
    @Autowired
    private GpioRepository gpioRepository;

    @Test
    void createUpdateAndDeletePump() {
        GpioBoard board = gpioRepository.getBoards().get(0);
        DcPump pump = new DcPump();
        pump.setName("RepoPump");
        pump.setPin(board.getPin(board.getMinPin()));
        pump.setTimePerClInMs(1000);
        pump.setIsPowerStateHigh(false);
        pump.setPumpedUp(false);
        pump.setFillingLevelInMl(100);
        pump.setTubeCapacityInMl(5.0);
        pump.setPowerConsumption(100);

        Pump created = pumpRepository.create(pump);
        assertNotNull(created);

        created.setName("RepoPumpUpdated");
        assertTrue(pumpRepository.update(created));

        Optional<Pump> found = pumpRepository.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("RepoPumpUpdated", found.get().getName());

        assertTrue(pumpRepository.delete(created.getId()));
    }

    @Test
    void createStepperPump() {
        GpioBoard board = gpioRepository.getBoards().get(0);
        StepperPump pump = new StepperPump();
        pump.setName("RepoStepper");
        pump.setStepPin(board.getPin(board.getMinPin()));
        pump.setEnablePin(board.getPin(board.getMinPin() + 1));
        pump.setStepsPerCl(20);
        pump.setMaxStepsPerSecond(200);
        pump.setAcceleration(5);
        pump.setPumpedUp(false);
        pump.setFillingLevelInMl(100);
        pump.setTubeCapacityInMl(5.0);
        pump.setPowerConsumption(100);

        Pump created = pumpRepository.create(pump);
        assertNotNull(created);

        List<Pump> all = pumpRepository.findAll();
        assertTrue(all.stream().anyMatch(p -> p.getId() == created.getId()));
    }
}
