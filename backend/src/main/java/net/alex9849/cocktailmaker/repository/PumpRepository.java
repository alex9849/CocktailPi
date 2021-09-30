package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PumpRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private IngredientRepository ingredientRepository;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public Pump create(Pump pump) {
        return getJdbcTemplate().execute((ConnectionCallback<Pump>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO pumps (gpio_pin, time_per_cl_in_ms, " +
                    "tube_capacity_in_ml, current_ingredient_id) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, pump.getGpioPin());
            pstmt.setInt(2, pump.getTimePerClInMs());
            pstmt.setInt(3, pump.getTubeCapacityInMl());
            pstmt.setObject(4, pump.getCurrentIngredientId());
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                pump.setId(rs.getLong(1));
                return populateEntity(pump);
            }
            throw new IllegalStateException("Error saving pump");
        });
    }

    public boolean update(Pump pump) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("UPDATE pumps SET gpio_pin = ?, time_per_cl_in_ms = ?, " +
                    "tube_capacity_in_ml = ?, current_ingredient_id = ? WHERE id = ?");
            pstmt.setInt(1, pump.getGpioPin());
            pstmt.setInt(2, pump.getTimePerClInMs());
            pstmt.setInt(3, pump.getTubeCapacityInMl());
            pstmt.setObject(4, pump.getCurrentIngredientId());
            pstmt.setLong(5, pump.getId());
            return pstmt.executeUpdate() != 0;
        });
    }

    public List<Pump> findAll() {
        return getJdbcTemplate().execute((ConnectionCallback<List<Pump>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM pumps");
            ResultSet rs = pstmt.executeQuery();
            List<Pump> results = new ArrayList<>();
            while (rs.next()) {
                results.add(populateEntity(parseRs(rs)));
            }
            return results;
        });
    }

    public Optional<Pump> findByGpioPin(int gpioPin) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<Pump>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM pumps where gpio_pin = ?");
            pstmt.setInt(1, gpioPin);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(populateEntity(parseRs(rs)));
            }
            return Optional.empty();
        });
    }

    public Optional<Pump> findById(long id) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<Pump>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM pumps where id = ?");
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(populateEntity(parseRs(rs)));
            }
            return Optional.empty();
        });
    }

    public boolean delete(long id) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE from pumps WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        });
    }

    public Pump populateEntity(Pump pump) {
        if(pump.getCurrentIngredientId() != null) {
            Ingredient ingredient = ingredientRepository.findById(pump.getCurrentIngredientId()).orElse(null);
            if(ingredient instanceof AutomatedIngredient) {
                pump.setCurrentIngredient((AutomatedIngredient) ingredient);
            }
        }
        return pump;
    }

    private Pump parseRs(ResultSet rs) throws SQLException {
        Pump pump = new Pump();
        pump.setId(rs.getLong("id"));
        pump.setGpioPin(rs.getInt("gpio_pin"));
        pump.setTimePerClInMs(rs.getInt("time_per_cl_in_ms"));
        pump.setTubeCapacityInMl(rs.getInt("tube_capacity_in_ml"));
        pump.setCurrentIngredientId(rs.getLong("current_ingredient_id"));
        return pump;
    }
}
