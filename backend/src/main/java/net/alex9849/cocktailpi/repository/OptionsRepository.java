package net.alex9849.cocktailpi.repository;

import jakarta.annotation.PostConstruct;
import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.gpio.HardwarePin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Optional;

@Component
public class OptionsRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private GpioRepository gpioRepository;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public Optional<String> getOption(String key) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<String>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT value FROM options o " +
                    "WHERE o.key = ?");
            pstmt.setString(1, key);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getString("value"));
            }
            return Optional.empty();
        });
    }

    public void setOption(String key, String value) {
        if(value != null) {
            getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
                PreparedStatement pstmt = con.prepareStatement("INSERT INTO options (key, value, pin_board, pin_nr)\n" +
                        "VALUES (?, ?, ?, ?)\n" +
                        "ON CONFLICT (key) DO UPDATE\n" +
                        "    SET value = excluded.value, pin_board = excluded.pin_board, pin_nr = excluded.pin_nr");
                pstmt.setString(1, key);
                pstmt.setString(2, value);
                pstmt.setNull(3, Types.INTEGER);
                pstmt.setNull(4, Types.INTEGER);

                pstmt.executeUpdate();
                return null;
            });
        } else {
            delOption(key, false);
        }
    }

    public void setPinOption(String key, HardwarePin hwPin) {
        if(hwPin != null) {
            getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
                PreparedStatement pstmt = con.prepareStatement("INSERT INTO options (key, value, pin_board, pin_nr)\n" +
                        "VALUES (?, ?, ?, ?)\n" +
                        "ON CONFLICT (key) DO UPDATE\n" +
                        "    SET value = excluded.value, pin_board = excluded.pin_board, pin_nr = excluded.pin_nr");
                pstmt.setString(1, key);
                pstmt.setNull(2, Types.VARCHAR);
                pstmt.setLong(3, hwPin.getBoardId());
                pstmt.setInt(4, hwPin.getPinNr());

                pstmt.executeUpdate();
                return null;
            });
        } else {
            delOption(key, false);
        }
    }

    public Optional<HardwarePin> getPinOption(String key) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<HardwarePin>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT pin_board, pin_nr FROM options WHERE key = ?");
            pstmt.setString(1, key);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                long boardId = rs.getLong("pin_board");
                if(rs.wasNull()) {
                    return Optional.empty();
                }
                GpioBoard gpioBoard = gpioRepository.findById(boardId).orElse(null);
                return Optional.of(gpioBoard.getPin((Integer) rs.getObject("pin_nr")));
            }
            return Optional.empty();
        });
    }

    public void delOption(String key, boolean like) {
        getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
            PreparedStatement pstmt;
            if(like) {
                pstmt = con.prepareStatement("DELETE FROM options AS o WHERE o.key LIKE ?");
            } else {
                pstmt = con.prepareStatement("DELETE FROM options AS o WHERE o.key = ?");
            }
            pstmt.setString(1, key);
            pstmt.executeUpdate();
            return null;
        });
    }
}
