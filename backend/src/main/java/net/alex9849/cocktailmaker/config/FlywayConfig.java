package net.alex9849.cocktailmaker.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

@Configuration
class FlywayConfig extends FlywayAutoConfiguration.FlywayConfiguration {

    @Primary
    @Bean(name = "flywayInitializer")
    @DependsOn("springUtility")
    public FlywayMigrationInitializer flywayInitializer(Flyway flyway,
                                                        ObjectProvider<FlywayMigrationStrategy> objectProvider){
        return super.flywayInitializer(flyway, objectProvider);
    }
}