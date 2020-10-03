package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.payload.dto.pump.PumpDto;
import net.alex9849.cocktailmaker.repository.PumpRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PumpService {

    @Autowired
    private PumpRepository pumpRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private IngredientService ingredientService;

    public List<Pump> getAllPumps() {
        return pumpRepository.findAll();
    }

    public Pump getPump(long id) {
        return pumpRepository.findById(id).orElse(null);
    }

    public Pump createPump(Pump pump) {
        if(pumpRepository.findByGpioPin(pump.getGpioPin()).isPresent()) {
            throw new IllegalArgumentException("GPOI-Pin already in use!");
        }
        Pump savedPump = pumpRepository.save(pump);
        webSocketService.broadcastPumpLayout(getAllPumps());
        return savedPump;
    }

    public Pump updatePump(Pump pump) {
        Optional<Pump> optPumpWithGpio = pumpRepository.findByGpioPin(pump.getGpioPin());
        if(optPumpWithGpio.isPresent()) {
            if(optPumpWithGpio.get().getId() != pump.getId()) {
                throw new IllegalArgumentException("GPOI-Pin already in use!");
            }
        } else {
            throw new IllegalArgumentException("Pump doesn't exist!");
        }
        Pump savedPump = pumpRepository.save(pump);
        webSocketService.broadcastPumpLayout(getAllPumps());
        return savedPump;
    }

    public List<Pump> getPumpsWithIngredient(List<Long> ingredientIds) {
        return pumpRepository.findAllByCurrentIngredient_IdIn(ingredientIds);
    }

    public Pump fromDto(PumpDto pumpDto) {
        if(pumpDto == null) {
            return null;
        }
        Pump pump = new Pump();
        BeanUtils.copyProperties(pumpDto, pump);
        pump.setCurrentIngredient(ingredientService.fromDto(pumpDto.getCurrentIngredient()));
        return pump;
    }

    public void deletePump(long id) {
        pumpRepository.deleteById(id);
        webSocketService.broadcastPumpLayout(getAllPumps());
    }
}
