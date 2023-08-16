package net.alex9849.cocktailmaker.repository;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailmaker.model.gpio.GpioBoard;
import net.alex9849.cocktailmaker.model.gpio.I2CGpioBoard;
import net.alex9849.cocktailmaker.model.gpio.LocalGpioBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    /*public List<GpioBoard.Pin> getBoardsByBoardId(long boardId) {
        return getJdbcTemplate().execute((ConnectionCallback<List<GpioBoard.Pin>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM gpio_pins where lower(dType) = lower(?)");
            pstmt.setString(1, dType);

            ResultSet rs = pstmt.executeQuery();
            List<GpioBoard> result = new ArrayList<>();
            while (rs.next()) {
                result.add(parseRs(rs));
            }
            return result;
        });
    }*/

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

    private Optional<GpioBoard> findById(long id) {
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
