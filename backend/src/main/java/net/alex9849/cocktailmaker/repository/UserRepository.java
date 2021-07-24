package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserRepository {
    private final DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<User> findById(long id) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM users WHERE id = ?");
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading user", throwables);
        }
    }

    public Optional<User> findByEmail(String email) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM users WHERE lower(email) = lower(?)");
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading user", throwables);
        }
    }

    public Optional<User> findByUsernameIgnoringCase(String username) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM users WHERE lower(username) = lower(?)");
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading user", throwables);
        }
    }

    public List<User> findAll() {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM users");
            ResultSet rs = pstmt.executeQuery();
            List<User> results = new ArrayList<>();
            if (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading user", throwables);
        }
    }

    public boolean delete(long id) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM users WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error deleting user", throwables);
        }
    }

    public User create(User user) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO users (email, username, firstname, " +
                    "lastname, password, is_account_non_locked, role) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getFirstname());
            pstmt.setString(4, user.getLastname());
            pstmt.setString(5, user.getPassword());
            pstmt.setBoolean(6, user.isAccountNonLocked());
            pstmt.setString(7, user.getAuthority().toString());

            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getLong(1));
                return user;
            }
            throw new IllegalStateException("Error saving user");
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error saving user", throwables);
        }
    }

    public boolean update(User user) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("UPDATE users SET email = ?, username = ?, " +
                    "firstname = ?, lastname = ?, password = ?, is_account_non_locked = ?, role = ? WHERE id = ?");
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getFirstname());
            pstmt.setString(4, user.getLastname());
            pstmt.setString(5, user.getPassword());
            pstmt.setBoolean(6, user.isAccountNonLocked());
            pstmt.setString(7, user.getAuthority().toString());
            pstmt.setLong(8, user.getId());
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error updating user", throwables);
        }
    }

    private User parseRs(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setAuthority(ERole.valueOf(rs.getString("role")));
        user.setEmail(rs.getString("email"));
        user.setFirstname(rs.getString("firstname"));
        user.setLastname(rs.getString("lastname"));
        user.setPassword(rs.getString("password"));
        user.setAccountNonLocked(rs.getBoolean("is_account_non_locked"));
        return user;
    }

}
