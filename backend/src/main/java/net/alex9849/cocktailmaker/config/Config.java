package net.alex9849.cocktailmaker.config;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.context.ContextBuilder;
import com.pi4j.plugin.linuxfs.provider.i2c.LinuxFsI2CProvider;
import com.pi4j.plugin.mock.platform.MockPlatform;
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
                ctxBuilder.autoDetect();
            } else {
                ctxBuilder.add(new MockPlatform());
                ctxBuilder.autoDetectProviders();
            }
            gpioController = ctxBuilder.build();
        }
        return gpioController;
    }


}
