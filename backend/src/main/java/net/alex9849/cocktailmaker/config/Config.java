package net.alex9849.cocktailmaker.config;

import net.alex9849.cocktailmaker.hardware.DemoModeGpioController;
import net.alex9849.cocktailmaker.hardware.IGpioController;
import net.alex9849.cocktailmaker.hardware.ProdModeGpioController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class Config {
    @Value("${alex9849.app.isRaspberryPi}")
    private boolean isRaspberryPi;

    private IGpioController gpioController;

    @Bean(destroyMethod = "shutdown")
    public IGpioController getGpioController() {
        if(gpioController == null) {
            if(isRaspberryPi) {
                gpioController = new ProdModeGpioController();
            } else {
                gpioController = new DemoModeGpioController();
            }
        }
        return gpioController;
    }


}
