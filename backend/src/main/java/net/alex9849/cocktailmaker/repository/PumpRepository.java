package net.alex9849.cocktailmaker.repository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.model.pump.StepperPump;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Component
public class PumpRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;
    private Map<Long, Pump> cachedPumps;


    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public Pump create(Pump pump) {
        return getJdbcTemplate().execute((ConnectionCallback<Pump>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO pumps (dtype, name, " +
                    "completed, enabled, tube_capacity, current_ingredient_id, filling_level_in_ml, " +
                    "is_pumped_up, dc_pin, time_per_cl_in_ms, is_power_state_high, acceleration, " +
                    "step_pin, enable_pin, steps_per_cl, max_steps_per_second) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            setParameters(pump, pstmt);
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                pump.setId(rs.getLong(1));
                return pump;
            }
            throw new IllegalStateException("Error saving pump");
        });
    }

    public boolean update(Pump pump) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("UPDATE pumps SET dtype = ?, name = ?, " +
                    "completed = ?, enabled = ?, tube_capacity = ?, current_ingredient_id = ?, " +
                    "filling_level_in_ml = ?, is_pumped_up = ?, dc_pin = ?, time_per_cl_in_ms = ?, " +
                    "is_power_state_high = ?, acceleration = ?, step_pin = ?, enable_pin = ?, " +
                    "steps_per_cl = ?, max_steps_per_second = ? WHERE id = ?");
            setParameters(pump, pstmt);
            pstmt.setLong(17, pump.getId());
            return pstmt.executeUpdate() != 0;
        });
    }

    public List<Pump> findAll() {
        return getJdbcTemplate().execute((ConnectionCallback<List<Pump>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM pumps");
            ResultSet rs = pstmt.executeQuery();
            List<Pump> results = new ArrayList<>();
            while (rs.next()) {
                results.add(parseRs(rs));
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
                results.add(parseRs(rs));
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
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM pumps where dc_pin = ? or enable_pin = ? or step_pin = ?");
            pstmt.setInt(1, bcmPin);
            pstmt.setInt(2, bcmPin);
            pstmt.setInt(3, bcmPin);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
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
                return Optional.of(parseRs(rs));
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

    private static void setParameters(Pump pump, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, pump.getClass().getAnnotation(DiscriminatorValue.class).value());
        pstmt.setObject(2, pump.getName());
        pstmt.setBoolean(3, pump.isCompleted());
        pstmt.setBoolean(4, pump.isEnabled());
        pstmt.setObject(5, pump.getTubeCapacityInMl());
        pstmt.setObject(6, pump.getCurrentIngredientId());
        pstmt.setObject(7, pump.getFillingLevelInMl());
        pstmt.setBoolean(8, pump.isPumpedUp());
        if(pump instanceof DcPump) {
            DcPump dcPump = (DcPump) pump;
            pstmt.setObject(9, dcPump.getBcmPin());
            pstmt.setObject(10, dcPump.getTimePerClInMs());
            pstmt.setObject(11, dcPump.isPowerStateHigh());
            pstmt.setNull(12, Types.INTEGER);
            pstmt.setNull(13, Types.INTEGER);
            pstmt.setNull(14, Types.INTEGER);
            pstmt.setNull(15, Types.INTEGER);
            pstmt.setNull(16, Types.INTEGER);
        }
        else if (pump instanceof StepperPump) {
            StepperPump stepperPump = (StepperPump) pump;
            pstmt.setNull(9, Types.INTEGER);
            pstmt.setNull(10, Types.INTEGER);
            pstmt.setNull(11, Types.BOOLEAN);
            pstmt.setObject(12, stepperPump.getAcceleration());
            pstmt.setObject(13, stepperPump.getStepPin());
            pstmt.setObject(14, stepperPump.getEnablePin());
            pstmt.setObject(15, stepperPump.getStepsPerCl());
            pstmt.setObject(16, stepperPump.getMaxStepsPerSecond());
        } else {
            throw new IllegalArgumentException("Unknown Pump type: " + pump.getClass().getName());
        }
    }

    private Pump parseRs(ResultSet rs) throws SQLException {
        Pump pump;
        String dType = rs.getString("dType");

        if(Objects.equals(dType, DcPump.class.getAnnotation(DiscriminatorValue.class).value())) {
            DcPump dcPump = new DcPump();
            dcPump.setBcmPin((Integer) rs.getObject("dc_pin"));
            dcPump.setTimePerClInMs((Integer) rs.getObject("time_per_cl_in_ms"));
            boolean isPowerStateHigh = rs.getBoolean("is_power_state_high");
            if(!rs.wasNull()) {
                dcPump.setPowerStateHigh(isPowerStateHigh);
            }
            pump = dcPump;
        } else if(Objects.equals(dType, StepperPump.class.getAnnotation(DiscriminatorValue.class).value())) {
            StepperPump stepperPump = new StepperPump();
            stepperPump.setAcceleration((Integer) rs.getObject("acceleration"));
            stepperPump.setStepPin((Integer) rs.getObject("step_pin"));
            stepperPump.setEnablePin((Integer) rs.getObject("enable_pin"));
            stepperPump.setStepsPerCl((Integer) rs.getObject("steps_per_cl"));
            stepperPump.setMaxStepsPerSecond((Integer) rs.getObject("max_steps_per_second"));
            pump = stepperPump;
        } else {
            throw new IllegalStateException("Unknown pump-dType: " + dType);
        }
        pump.setId(rs.getLong("id"));
        pump.setName((String) rs.getObject("name"));
        pump.setEnabled(rs.getBoolean("enabled"));
        pump.setTubeCapacityInMl((Double) rs.getObject("tube_capacity"));
        long ingredientId = rs.getLong("current_ingredient_id");
        if(!rs.wasNull()) {
            pump.setCurrentIngredientId(ingredientId);
        }
        pump.setFillingLevelInMl((Integer) rs.getObject("filling_level_in_ml"));
        pump.setPumpedUp(rs.getBoolean("is_pumped_up"));
        return pump;
    }
}
