package net.alex9849.cocktailmaker.config;

import net.alex9849.cocktailmaker.iface.IGpioController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class Config {
    @Value("${alex9849.app.demoMode}")
    private boolean demoMode;

    private IGpioController gpioController;

    @Bean
    public IGpioController getGpioController() {
        if(gpioController == null) {
            try {
                if(demoMode) {
                    gpioController = (IGpioController) Class.forName("net.alex9849.cocktailmaker.iface.DemomodeGpioController").newInstance();
                } else {
                    gpioController = (IGpioController) Class.forName("net.alex9849.cocktailmaker.iface.ProdModeGpioController").newInstance();
                }
            } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return gpioController;
    }
}
