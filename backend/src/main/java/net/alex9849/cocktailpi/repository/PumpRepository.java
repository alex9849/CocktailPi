package net.alex9849.cocktailpi.repository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.pump.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Component
public class PumpRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private GpioRepository gpioRepository;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public Pump create(Pump pump) {
        return getJdbcTemplate().execute((ConnectionCallback<Pump>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO pumps (dtype, name, " +
                    "completed, tube_capacity, current_ingredient_id, filling_level_in_ml, " +
                    "is_pumped_up, power_consumption, oo_pin_board, oo_pin_nr, time_per_cl_in_ms, is_power_state_high, acceleration, " +
                    "step_pin_board, step_pin_nr, enable_pin_board, enable_pin_nr, steps_per_cl, max_steps_per_second) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
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
                    "completed = ?, tube_capacity = ?, current_ingredient_id = ?, " +
                    "filling_level_in_ml = ?, is_pumped_up = ?, power_consumption = ?, oo_pin_board = ?, oo_pin_nr = ?, time_per_cl_in_ms = ?, " +
                    "is_power_state_high = ?, acceleration = ?, step_pin_board = ?, step_pin_nr = ?, enable_pin_board = ?, " +
                    "enable_pin_nr = ?, steps_per_cl = ?, max_steps_per_second = ? WHERE id = ?");
            setParameters(pump, pstmt);
            pstmt.setLong(20, pump.getId());
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
        int idx = 1;
        pstmt.setString(idx++, pump.getClass().getAnnotation(DiscriminatorValue.class).value());
        pstmt.setObject(idx++, pump.getName());
        pstmt.setBoolean(idx++, pump.isCompleted());
        pstmt.setObject(idx++, pump.getTubeCapacityInMl());
        pstmt.setObject(idx++, pump.getCurrentIngredientId());
        pstmt.setObject(idx++, pump.getFillingLevelInMl());
        pstmt.setBoolean(idx++, pump.isPumpedUp());
        pstmt.setInt(idx++, pump.getPowerConsumption());
        if(pump instanceof OnOffPump onOffPump) {
            if(onOffPump.getPin() != null) {
                pstmt.setObject(idx++, onOffPump.getPin().getBoardId());
                pstmt.setObject(idx++, onOffPump.getPin().getPinNr());
            } else {
                pstmt.setNull(idx++, Types.INTEGER);
                pstmt.setNull(idx++, Types.INTEGER);
            }
            if(onOffPump instanceof DcPump dcPump) {
                pstmt.setObject(idx++, dcPump.getTimePerClInMs());
            } else if (onOffPump instanceof Valve valve) {
                pstmt.setObject(idx++, valve.getTimePerClInMs());
            } else {
                pstmt.setNull(idx++, Types.INTEGER);
            }
            pstmt.setObject(idx++, onOffPump.isPowerStateHigh());
            pstmt.setNull(idx++, Types.INTEGER);
            pstmt.setNull(idx++, Types.INTEGER);
            pstmt.setNull(idx++, Types.INTEGER);
            pstmt.setNull(idx++, Types.INTEGER);
            pstmt.setNull(idx++, Types.INTEGER);
            pstmt.setNull(idx++, Types.INTEGER);
            pstmt.setNull(idx++, Types.INTEGER);
        }
        else if (pump instanceof StepperPump stepperPump) {
            pstmt.setNull(idx++, Types.INTEGER);
            pstmt.setNull(idx++, Types.INTEGER);
            pstmt.setNull(idx++, Types.INTEGER);
            pstmt.setNull(idx++, Types.BOOLEAN);
            pstmt.setObject(idx++, stepperPump.getAcceleration());
            if(stepperPump.getStepPin() != null) {
                pstmt.setObject(idx++, stepperPump.getStepPin().getBoardId());
                pstmt.setObject(idx++, stepperPump.getStepPin().getPinNr());
            } else {
                pstmt.setNull(idx++, Types.INTEGER);
                pstmt.setNull(idx++, Types.INTEGER);
            }
            if(stepperPump.getEnablePin() != null) {
                pstmt.setObject(idx++, stepperPump.getEnablePin().getBoardId());
                pstmt.setObject(idx++, stepperPump.getEnablePin().getPinNr());
            } else {
                pstmt.setNull(idx++, Types.INTEGER);
                pstmt.setNull(idx++, Types.INTEGER);
            }
            pstmt.setObject(idx++, stepperPump.getStepsPerCl());
            pstmt.setObject(idx++, stepperPump.getMaxStepsPerSecond());
        } else {
            throw new IllegalArgumentException("Unknown Pump type: " + pump.getClass().getName());
        }
    }

    private Pump parseRs(ResultSet rs) throws SQLException {
        Pump pump;
        String dType = rs.getString("dType");

        if(Objects.equals(dType, DcPump.class.getAnnotation(DiscriminatorValue.class).value())
                || Objects.equals(dType, Valve.class.getAnnotation(DiscriminatorValue.class).value())) {
            OnOffPump onOffPump;
            if(Objects.equals(dType, DcPump.class.getAnnotation(DiscriminatorValue.class).value())) {
                DcPump dcPump = new DcPump();
                dcPump.setTimePerClInMs((Integer) rs.getObject("time_per_cl_in_ms"));
                onOffPump = dcPump;
            } else {
                Valve valve = new Valve();
                valve.setTimePerClInMs((Integer) rs.getObject("time_per_cl_in_ms"));
                onOffPump = valve;
            }
            long boardId = rs.getLong("oo_pin_board");
            if(!rs.wasNull()) {
                GpioBoard gpioBoard = gpioRepository.findById(boardId).orElse(null);
                onOffPump.setPin(gpioBoard.getPin((Integer) rs.getObject("oo_pin_nr")));
            }
            boolean isPowerStateHigh = rs.getBoolean("is_power_state_high");
            if(!rs.wasNull()) {
                onOffPump.setIsPowerStateHigh(isPowerStateHigh);
            }
            pump = onOffPump;
        } else if(Objects.equals(dType, StepperPump.class.getAnnotation(DiscriminatorValue.class).value())) {
            StepperPump stepperPump = new StepperPump();
            stepperPump.setAcceleration((Integer) rs.getObject("acceleration"));
            long stepBoardId = rs.getLong("step_pin_board");
            if(!rs.wasNull()) {
                GpioBoard gpioBoard = gpioRepository.findById(stepBoardId).orElse(null);
                stepperPump.setStepPin(gpioBoard.getPin((Integer) rs.getObject("step_pin_nr")));
            }
            long enableBoardId = rs.getLong("enable_pin_board");
            if(!rs.wasNull()) {
                GpioBoard gpioBoard = gpioRepository.findById(enableBoardId).orElse(null);
                stepperPump.setEnablePin(gpioBoard.getPin((Integer) rs.getObject("enable_pin_nr")));
            }
            stepperPump.setStepsPerCl((Integer) rs.getObject("steps_per_cl"));
            stepperPump.setMaxStepsPerSecond((Integer) rs.getObject("max_steps_per_second"));
            pump = stepperPump;
        } else {
            throw new IllegalStateException("Unknown pump-dType: " + dType);
        }
        pump.setId(rs.getLong("id"));
        pump.setName((String) rs.getObject("name"));
        pump.setTubeCapacityInMl((Double) rs.getObject("tube_capacity"));
        pump.setPowerConsumption(rs.getInt("power_consumption"));
        long ingredientId = rs.getLong("current_ingredient_id");
        if(!rs.wasNull()) {
            pump.setCurrentIngredientId(ingredientId);
        }
        pump.setFillingLevelInMl((Integer) rs.getObject("filling_level_in_ml"));
        pump.setPumpedUp(rs.getBoolean("is_pumped_up"));
        return pump;
    }
}
