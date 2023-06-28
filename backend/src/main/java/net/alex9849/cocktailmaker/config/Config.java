package net.alex9849.cocktailmaker.config;

import net.alex9849.cocktailmaker.iface.IGpioController;
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
            try {
                if(isRaspberryPi) {
                    gpioController = (IGpioController) Class.forName("net.alex9849.cocktailmaker.iface.ProdModeGpioController").newInstance();
                } else {
                    gpioController = (IGpioController) Class.forName("net.alex9849.cocktailmaker.iface.DemoModeGpioController").newInstance();
                }
            } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return gpioController;
    }


}
