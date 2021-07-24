package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.Pump;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PumpRepository {
    private final DataSource dataSource;

    public PumpRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Pump create(Pump pump) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO pumps (gpio_pin, time_per_cl_in_ms, " +
                    "tube_capacity_in_ml, current_ingredient_id) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, pump.getGpioPin());
            pstmt.setInt(2, pump.getTimePerClInMs());
            pstmt.setInt(3, pump.getTubeCapacityInMl());
            pstmt.setLong(4, pump.getCurrentIngredientId());
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                pump.setId(rs.getLong(1));
                return pump;
            }
            throw new IllegalStateException("Error saving pump");
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error saving pump", throwables);
        }
    }

    public boolean update(Pump pump) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("UPDATE pumps SET gpio_pin = ?, time_per_cl_in_ms = ?, " +
                    "tube_capacity_in_ml = ?, current_ingredient_id = ? WHERE id = ?");
            pstmt.setInt(1, pump.getGpioPin());
            pstmt.setInt(2, pump.getTimePerClInMs());
            pstmt.setInt(3, pump.getTubeCapacityInMl());
            pstmt.setLong(4, pump.getCurrentIngredientId());
            pstmt.setLong(5, pump.getId());
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error updating pump", throwables);
        }
    }

    public List<Pump> findAll() {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM pumps");
            ResultSet rs = pstmt.executeQuery();
            List<Pump> results = new ArrayList<>();
            while (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error saving pump", throwables);
        }
    }

    public Optional<Pump> findByGpioPin(int gpioPin) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM pumps where gpio_pin = ?");
            pstmt.setInt(1, gpioPin);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading pump", throwables);
        }
    }

    public Optional<Pump> findById(long id) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM pumps where id = ?");
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading pump", throwables);
        }
    }

    public boolean delete(long id) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("DELETE from pumps WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error deleting pump", throwables);
        }
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
