package net.alex9849.cocktailmaker.repository;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailmaker.model.gpio.GpioBoard;
import net.alex9849.cocktailmaker.model.gpio.I2CGpioBoard;
import net.alex9849.cocktailmaker.model.gpio.LocalGpioBoard;
import net.alex9849.cocktailmaker.model.gpio.PinResource;
import net.alex9849.cocktailmaker.service.GpioService;
import net.alex9849.cocktailmaker.service.pumps.PumpMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.IntStream;

@Component
public class GpioRepository extends JdbcDaoSupport {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public List<GpioBoard> getBoards() {
        return getJdbcTemplate().execute((ConnectionCallback<List<GpioBoard>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM gpio_boards");

            ResultSet rs = pstmt.executeQuery();
            List<GpioBoard> result = new ArrayList<>();
            while (rs.next()) {
                result.add(parseRs(rs));
            }
            return result;
        });
    }

    public List<GpioBoard> getBoardsByDType(String dType) {
        return getJdbcTemplate().execute((ConnectionCallback<List<GpioBoard>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM gpio_boards where lower(dType) = lower(?)");
            pstmt.setString(1, dType);

            ResultSet rs = pstmt.executeQuery();
            List<GpioBoard> result = new ArrayList<>();
            while (rs.next()) {
                result.add(parseRs(rs));
            }
            return result;
        });
    }

    public Optional<PinResource> getPinResourceByBoardIdAndPin(long boardId, int pinNr) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<PinResource>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT pi.pin_nr, p.id AS pump_id, p.name AS pump_name, o.key AS o_key\n" +
                    "FROM gpio_pins pi\n" +
                    "         LEFT JOIN pumps p ON (p.dc_pin_nr = pi.pin_nr AND p.dc_pin_board = pi.board)" +
                    "                  OR (p.enable_pin_nr = pi.pin_nr AND p.enable_pin_board = pi.board)" +
                    "                  OR (p.step_pin_nr = pi.pin_nr AND p.step_pin_board = pi.board)\n" +
                    "         LEFT JOIN options o ON pi.pin_nr = o.pin_nr AND pi.board = o.pin_board\n" +
                    "WHERE pi.board = ? AND pi.pin_nr = ?");
            pstmt.setLong(1, boardId);
            pstmt.setInt(2, pinNr);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.ofNullable(parsePinResource(rs));
            }
            return Optional.empty();
        });
    }

    private PinResource parsePinResource(ResultSet rs) throws SQLException {
        PinResource pr = null;
        if(rs.getObject("pump_id") != null) {
            pr = new PinResource();
            pr.setId((int) rs.getObject("pump_id"));
            pr.setType(PinResource.Type.PUMP);
            String pump_name = (String) rs.getObject("pump_name");
            if(pump_name == null) {
                pump_name = "Pump " + pr.getId();
            }
            pr.setName(pump_name);

        } else if (rs.getObject("o_key") != null) {
            pr = new PinResource();
            switch (rs.getString("o_key")) {
                case PumpMaintenanceService.REPO_KEY_PUMP_DIRECTION_PIN:
                    pr.setType(PinResource.Type.PUMP_DIRECTION);
                    pr.setName("Pump direction");
                    break;
                case GpioService.REPO_KEY_I2C_PIN_SDA:
                    pr.setType(PinResource.Type.I2C);
                    pr.setName("I2C SDA");
                    break;
                case GpioService.REPO_KEY_I2C_PIN_SCL:
                    pr.setType(PinResource.Type.I2C);
                    pr.setName("I2C SCL");
                    break;
                default:
                    pr = null;
            }
        }
        return pr;
    }

    public GpioBoard createBoard(GpioBoard gpioBoard) {
        return getJdbcTemplate().execute((ConnectionCallback<GpioBoard>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO gpio_boards (name, dType, board_model, i2c_address) " +
                    "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, gpioBoard.getName());
            pstmt.setString(2, gpioBoard.getClass().getAnnotation(DiscriminatorValue.class).value());

            if(gpioBoard instanceof I2CGpioBoard i2CGpioBoard) {
                pstmt.setString(3, i2CGpioBoard.getBoardModel().name());
                pstmt.setInt(4, i2CGpioBoard.getI2cAddress());

            } else if (gpioBoard instanceof LocalGpioBoard) {
                pstmt.setNull(3, Types.VARCHAR);
                pstmt.setNull(4, Types.INTEGER);

            } else {
                throw new IllegalStateException("Unknown GPIO-board type: " + gpioBoard.getName());
            }

            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                long boardId = rs.getLong(1);
                createPins(boardId, gpioBoard.getMinPin(), gpioBoard.getMaxPin());
                return findById(boardId).orElse(null);
            }
            throw new IllegalStateException("Error saving gpio board");
        });
    }

    private void createPins(long boardId, int minPin, int maxPin) {
        String valueString = String.join(", ", IntStream.range(minPin, maxPin + 1)
                .mapToObj(x -> "(?, ?)").toArray(String[]::new));

        getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO gpio_pins (pin_nr, board) " +
                    "VALUES " + valueString);
            for(int i = 0; i <= (maxPin - minPin); i++) {
                pstmt.setInt(2 * i + 1, minPin + i);
                pstmt.setLong(2 * i + 2, boardId);
            }
            pstmt.execute();
            return null;
        });
    }

    public Optional<GpioBoard> findById(long id) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<GpioBoard>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * from gpio_boards WHERE id = ?");
            pstmt.setLong(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        });
    }

    private GpioBoard parseRs(ResultSet rs) throws SQLException {
        String dType = rs.getString("dType");
        GpioBoard gpioBoard;
        if(dType.equals(I2CGpioBoard.class.getAnnotation(DiscriminatorValue.class).value())) {
            String boardModel = rs.getString("board_model");
            I2CGpioBoard i2CGpioBoard = new I2CGpioBoard(I2CGpioBoard.BoardModel.valueOf(boardModel));
            i2CGpioBoard.setI2cAddress(rs.getByte("i2c_address"));
            gpioBoard = i2CGpioBoard;
        } else if (dType.equals(LocalGpioBoard.class.getAnnotation(DiscriminatorValue.class).value())) {
            gpioBoard = new LocalGpioBoard();
        } else {
            throw new IllegalArgumentException("GpioBoard-Type doesn't exist: " + dType);
        }
        gpioBoard.setId(rs.getLong("id"));
        gpioBoard.setName(rs.getString("name"));
        return gpioBoard;
    }

}
