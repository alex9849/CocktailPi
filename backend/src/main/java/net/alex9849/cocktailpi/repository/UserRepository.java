package net.alex9849.cocktailpi.repository;

import jakarta.annotation.PostConstruct;
import net.alex9849.cocktailpi.model.user.ERole;
import net.alex9849.cocktailpi.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public Optional<User> findById(long id) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<User>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM users WHERE id = ?");
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        });
    }

    public Optional<User> findByUsernameIgnoringCase(String username) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<User>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM users WHERE lower(username) = lower(?)");
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        });
    }

    public List<User> findAll() {
        return getJdbcTemplate().execute((ConnectionCallback<List<User>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM users ORDER BY username ASC ");
            ResultSet rs = pstmt.executeQuery();
            List<User> results = new ArrayList<>();
            while (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        });
    }

    public boolean delete(long id) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM users WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        });
    }

    public User create(User user) {
        return getJdbcTemplate().execute((ConnectionCallback<User>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO users (username, " +
                    "password, is_account_non_locked, role) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setBoolean(3, user.isAccountNonLocked());
            pstmt.setString(4, user.getAuthority().toString());

            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getLong(1));
                return user;
            }
            throw new IllegalStateException("Error saving user");
        });
    }

    public boolean update(User user) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("UPDATE users SET username = ?, " +
                    "password = ?, is_account_non_locked = ?, role = ? WHERE id = ?");
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setBoolean(3, user.isAccountNonLocked());
            pstmt.setString(4, user.getAuthority().toString());
            pstmt.setLong(5, user.getId());
            return pstmt.executeUpdate() != 0;
        });
    }

    private User parseRs(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setAuthority(ERole.valueOf(rs.getString("role")));
        user.setPassword(rs.getString("password"));
        user.setAccountNonLocked(rs.getBoolean("is_account_non_locked"));
        return user;
    }

}
