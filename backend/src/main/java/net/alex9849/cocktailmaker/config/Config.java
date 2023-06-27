package net.alex9849.cocktailmaker.config;

import net.alex9849.cocktailmaker.iface.IGpioController;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
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
    public DataSource getDataSource() {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:cocktailmaker-data.db?foreign_keys=on");
        dataSource.setAutoCommit(false);
        dataSource.setSuppressClose(true);
        return dataSource;
    }

}
