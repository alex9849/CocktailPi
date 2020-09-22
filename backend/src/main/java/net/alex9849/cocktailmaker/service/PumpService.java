package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.repository.PumpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PumpService {

    @Autowired
    private PumpRepository pumpRepository;

    public List<Pump> getAllPumps() {
        return pumpRepository.findAll();
    }

}
