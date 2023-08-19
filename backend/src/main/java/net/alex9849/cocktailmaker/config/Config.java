package net.alex9849.cocktailmaker.config;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.context.ContextBuilder;
import com.pi4j.library.pigpio.PiGpio;
import com.pi4j.library.pigpio.impl.PiGpioNativeImpl;
import com.pi4j.plugin.mock.platform.MockPlatform;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalInputProvider;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;
import com.pi4j.plugin.pigpio.provider.i2c.PiGpioI2CProvider;
import com.pi4j.plugin.pigpio.provider.pwm.PiGpioPwmProvider;
import com.pi4j.plugin.pigpio.provider.serial.PiGpioSerialProvider;
import com.pi4j.plugin.pigpio.provider.spi.PiGpioSpiProvider;
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
                //ctxBuilder.add(LinuxFsI2CProvider.newInstance());
                PiGpio piGpio = PiGpioNativeImpl.newInstance();
                ctxBuilder.add(PiGpioDigitalInputProvider.newInstance(piGpio));
                ctxBuilder.add(PiGpioDigitalOutputProvider.newInstance(piGpio));
                ctxBuilder.add(PiGpioSpiProvider.newInstance(piGpio));
                ctxBuilder.add(PiGpioSerialProvider.newInstance(piGpio));
                ctxBuilder.add(PiGpioI2CProvider.newInstance(piGpio));
                ctxBuilder.add(PiGpioPwmProvider.newInstance(piGpio));
            } else {
                ctxBuilder.add(new MockPlatform());
            }
            gpioController = ctxBuilder.build();
        }
        return gpioController;
    }


}
