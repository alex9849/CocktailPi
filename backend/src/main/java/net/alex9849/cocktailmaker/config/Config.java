package net.alex9849.cocktailmaker.config;

import net.alex9849.cocktailmaker.iface.IGpioController;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.DatabaseStartupValidator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.stream.Stream;

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

    @Bean
    public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource) {
        var dsv = new DatabaseStartupValidator();
        dsv.setDataSource(dataSource);
        dsv.setTimeout(120);
        dsv.setInterval(7);
        return dsv;
    }

    @Bean
    public static BeanFactoryPostProcessor dependsOnPostProcessor() {
        return bf -> {
            // Let beans that need the database depend on the DatabaseStartupValidator
            // like the JPA EntityManagerFactory or Flyway
            String[] flyway = bf.getBeanNamesForType(Flyway.class);
            Stream.of(flyway)
                    .map(bf::getBeanDefinition)
                    .forEach(it -> it.setDependsOn("databaseStartupValidator"));

            String[] jpa = bf.getBeanNamesForType(EntityManagerFactory.class);
            Stream.of(jpa)
                    .map(bf::getBeanDefinition)
                    .forEach(it -> it.setDependsOn("databaseStartupValidator"));
        };
    }

}
