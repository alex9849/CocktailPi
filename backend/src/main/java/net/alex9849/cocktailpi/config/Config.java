package net.alex9849.cocktailpi.config;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.context.ContextBuilder;
import com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalInputProvider;
import com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalOutputProvider;
import com.pi4j.plugin.linuxfs.provider.i2c.LinuxFsI2CProvider;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInputProvider;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutputProvider;
import com.pi4j.plugin.mock.provider.i2c.MockI2CProvider;
import com.pi4j.plugin.mock.provider.pwm.MockPwmProvider;
import com.pi4j.plugin.mock.provider.serial.MockSerialProvider;
import com.pi4j.plugin.mock.provider.spi.MockSpiProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class Config {
    @Value("${alex9849.app.isRaspberryPi}")
    private boolean isRaspberryPi;

    private Context gpioController;

    @Bean(destroyMethod = "shutdown")
    public Context getGpioController() {
        if(gpioController == null) {
            ContextBuilder ctxBuilder = Pi4J.newContextBuilder();
            if(isRaspberryPi) {
                ctxBuilder.add(LinuxFsI2CProvider.newInstance());
                ctxBuilder.add(GpioDDigitalInputProvider.newInstance());
                ctxBuilder.add(GpioDDigitalOutputProvider.newInstance());
            } else {
                ctxBuilder.add(MockDigitalInputProvider.newInstance());
                ctxBuilder.add(MockDigitalOutputProvider.newInstance());
                ctxBuilder.add(MockSpiProvider.newInstance());
                ctxBuilder.add(MockSerialProvider.newInstance());
                ctxBuilder.add(MockI2CProvider.newInstance());
                ctxBuilder.add(MockPwmProvider.newInstance());
            }
            gpioController = ctxBuilder.build();
        }
        return gpioController;
    }


}
