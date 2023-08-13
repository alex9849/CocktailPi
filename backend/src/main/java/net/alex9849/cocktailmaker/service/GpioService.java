package net.alex9849.cocktailmaker.service;

import com.pi4j.Pi4J;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GpioService {


    public void getGpioBackends() {

    }

    public void createGpioBackend() {

    }

    public void deleteGpioBackend() {

    }

    public void checkI2C() {
        //Delegate to system service
    }

    public void enableI2C() {
        //Delegate to system service
    }

}
