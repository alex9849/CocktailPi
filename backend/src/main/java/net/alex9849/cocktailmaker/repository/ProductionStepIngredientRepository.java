package net.alex9849.cocktailmaker.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

public class ProductionStepIngredientRepository extends JdbcDaoSupport {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private IngredientRepository ingredientRepository;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }


}
