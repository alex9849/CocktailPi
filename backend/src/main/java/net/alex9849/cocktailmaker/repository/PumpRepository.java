package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
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
import java.util.Set;

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
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO pumps (bcm_pin, time_per_cl_in_ms, " +
                    "tube_capacity_in_ml, current_ingredient_id, filling_level_in_ml, is_power_state_high, is_pumped_up) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, pump.getBcmPin());
            pstmt.setInt(2, pump.getTimePerClInMs());
            pstmt.setInt(3, pump.getTubeCapacityInMl());
            pstmt.setObject(4, pump.getCurrentIngredientId());
            pstmt.setInt(5, pump.getFillingLevelInMl());
            pstmt.setBoolean(6, pump.isPowerStateHigh());
            pstmt.setBoolean(7, pump.isPumpedUp());
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
            PreparedStatement pstmt = con.prepareStatement("UPDATE pumps SET bcm_pin = ?, time_per_cl_in_ms = ?, " +
                    "tube_capacity_in_ml = ?, current_ingredient_id = ?, filling_level_in_ml = ?, is_power_state_high = ?, is_pumped_up = ? WHERE id = ?");
            pstmt.setInt(1, pump.getBcmPin());
            pstmt.setInt(2, pump.getTimePerClInMs());
            pstmt.setInt(3, pump.getTubeCapacityInMl());
            pstmt.setObject(4, pump.getCurrentIngredientId());
            pstmt.setInt(5, pump.getFillingLevelInMl());
            pstmt.setBoolean(6, pump.isPowerStateHigh());
            pstmt.setBoolean(7, pump.isPumpedUp());
            pstmt.setLong(8, pump.getId());
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

    public List<Pump> findPumpsWithIngredient(long ingredientId) {
        return getJdbcTemplate().execute((ConnectionCallback<List<Pump>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM pumps where current_ingredient_id = ?");
            pstmt.setLong(1, ingredientId);
            ResultSet rs = pstmt.executeQuery();
            List<Pump> results = new ArrayList<>();
            while (rs.next()) {
                results.add(populateEntity(parseRs(rs)));
            }
            return results;
        });
    }

    public Set<Long> findIngredientIdsOnPump() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT p.current_ingredient_id as id FROM pumps p WHERE p.current_ingredient_id IS NOT NULL");
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Optional<Pump> findByBcmPin(int bcmPin) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<Pump>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM pumps where bcm_pin = ?");
            pstmt.setInt(1, bcmPin);
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
        pump.setBcmPin(rs.getInt("bcm_pin"));
        pump.setTimePerClInMs(rs.getInt("time_per_cl_in_ms"));
        pump.setTubeCapacityInMl(rs.getInt("tube_capacity_in_ml"));
        pump.setCurrentIngredientId(rs.getObject("current_ingredient_id", Long.class));
        pump.setFillingLevelInMl(rs.getInt("filling_level_in_ml"));
        pump.setPowerStateHigh(rs.getBoolean("is_power_state_high"));
        pump.setPumpedUp(rs.getBoolean("is_pumped_up"));
        return pump;
    }
}
