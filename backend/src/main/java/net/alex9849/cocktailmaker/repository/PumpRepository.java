package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.Pump;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PumpRepository extends JpaRepository<Pump, Long> {

    Optional<Pump> findByGpioPin(int gpoiPin);

}
