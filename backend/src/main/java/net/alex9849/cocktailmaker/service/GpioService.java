package net.alex9849.cocktailmaker.service;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailmaker.model.gpio.GpioBoard;
import net.alex9849.cocktailmaker.model.gpio.I2CGpioBoard;
import net.alex9849.cocktailmaker.model.gpio.LocalGpioBoard;
import net.alex9849.cocktailmaker.repository.GpioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GpioService {

    @Autowired
    private GpioRepository gpioRepository;


    public List<GpioBoard> getGpioBoards() {
        return gpioRepository.getBoards();
    }

    public List<GpioBoard> getGpioBoardsByType(String dType) {
        return gpioRepository.getBoardsByDType(dType);
    }

    public GpioBoard createGpioBoard(GpioBoard gpioBoard) {
        List<GpioBoard> byType = getGpioBoardsByType(LocalGpioBoard.class
                .getAnnotation(DiscriminatorValue.class).value());

        if(gpioBoard instanceof LocalGpioBoard) {
            if(!byType.isEmpty()) {
                throw new IllegalArgumentException("Can't create more than one LocalGpioBoard!");
            }
        } else if(gpioBoard instanceof I2CGpioBoard i2CGpioBoard) {
            if(byType.stream().anyMatch(x -> x.getName().equalsIgnoreCase(gpioBoard.getName()))) {
                throw new IllegalArgumentException("Board name already in use!");
            }
            if(byType.stream().map(x -> (I2CGpioBoard) x).anyMatch(x -> x.getI2cAddress() == i2CGpioBoard.getI2cAddress())) {
                throw new IllegalArgumentException("I2C-Address already in use!");
            }
        } else {
            throw new IllegalStateException("Unknown board type: " + gpioBoard.getClass());
        }
        return gpioRepository.createBoard(gpioBoard);
    }

    public void deleteGpioBoard() {

    }

    public void checkI2C() {
        //Delegate to system service
    }

    public void enableI2C() {
        //Delegate to system service
    }

}
