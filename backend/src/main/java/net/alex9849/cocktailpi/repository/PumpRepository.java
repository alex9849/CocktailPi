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
                    "is_pumped_up, oo_pin_board, oo_pin_nr, time_per_cl_in_ms, is_power_state_high, acceleration, " +
                    "step_pin_board, step_pin_nr, enable_pin_board, enable_pin_nr, steps_per_cl, max_steps_per_second) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
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
                    "filling_level_in_ml = ?, is_pumped_up = ?, oo_pin_board = ?, oo_pin_nr = ?, time_per_cl_in_ms = ?, " +
                    "is_power_state_high = ?, acceleration = ?, step_pin_board = ?, step_pin_nr = ?, enable_pin_board = ?, " +
                    "enable_pin_nr = ?, steps_per_cl = ?, max_steps_per_second = ? WHERE id = ?");
            setParameters(pump, pstmt);
            pstmt.setLong(19, pump.getId());
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
        pstmt.setString(1, pump.getClass().getAnnotation(DiscriminatorValue.class).value());
        pstmt.setObject(2, pump.getName());
        pstmt.setBoolean(3, pump.isCompleted());
        pstmt.setObject(4, pump.getTubeCapacityInMl());
        pstmt.setObject(5, pump.getCurrentIngredientId());
        pstmt.setObject(6, pump.getFillingLevelInMl());
        pstmt.setBoolean(7, pump.isPumpedUp());
        if(pump instanceof OnOffPump onOffPump) {
            if(onOffPump.getPin() != null) {
                pstmt.setObject(8, onOffPump.getPin().getBoardId());
                pstmt.setObject(9, onOffPump.getPin().getPinNr());
            } else {
                pstmt.setNull(8, Types.INTEGER);
                pstmt.setNull(9, Types.INTEGER);
            }
            if(onOffPump instanceof DcPump dcPump) {
                pstmt.setObject(10, dcPump.getTimePerClInMs());
            } else if (onOffPump instanceof Valve valve) {
                pstmt.setObject(10, valve.getTimePerClInMs());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }
            pstmt.setObject(11, onOffPump.isPowerStateHigh());
            pstmt.setNull(12, Types.INTEGER);
            pstmt.setNull(13, Types.INTEGER);
            pstmt.setNull(14, Types.INTEGER);
            pstmt.setNull(15, Types.INTEGER);
            pstmt.setNull(16, Types.INTEGER);
            pstmt.setNull(17, Types.INTEGER);
            pstmt.setNull(18, Types.INTEGER);
        }
        else if (pump instanceof StepperPump stepperPump) {
            pstmt.setNull(8, Types.INTEGER);
            pstmt.setNull(9, Types.INTEGER);
            pstmt.setNull(10, Types.INTEGER);
            pstmt.setNull(11, Types.BOOLEAN);
            pstmt.setObject(12, stepperPump.getAcceleration());
            if(stepperPump.getStepPin() != null) {
                pstmt.setObject(13, stepperPump.getStepPin().getBoardId());
                pstmt.setObject(14, stepperPump.getStepPin().getPinNr());
            } else {
                pstmt.setNull(13, Types.INTEGER);
                pstmt.setNull(14, Types.INTEGER);
            }
            if(stepperPump.getEnablePin() != null) {
                pstmt.setObject(15, stepperPump.getEnablePin().getBoardId());
                pstmt.setObject(16, stepperPump.getEnablePin().getPinNr());
            } else {
                pstmt.setNull(15, Types.INTEGER);
                pstmt.setNull(16, Types.INTEGER);
            }
            pstmt.setObject(17, stepperPump.getStepsPerCl());
            pstmt.setObject(18, stepperPump.getMaxStepsPerSecond());
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
        long ingredientId = rs.getLong("current_ingredient_id");
        if(!rs.wasNull()) {
            pump.setCurrentIngredientId(ingredientId);
        }
        pump.setFillingLevelInMl((Integer) rs.getObject("filling_level_in_ml"));
        pump.setPumpedUp(rs.getBoolean("is_pumped_up"));
        return pump;
    }
}
