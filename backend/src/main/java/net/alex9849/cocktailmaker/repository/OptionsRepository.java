package net.alex9849.cocktailmaker.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class OptionsRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public String getOption(String key) {
        return getJdbcTemplate().execute((ConnectionCallback<String>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT value FROM options o " +
                    "WHERE o.key = ?");
            pstmt.setString(1, key);

            ResultSet rs = pstmt.executeQuery();
            List<UUID> results = new ArrayList<>();
            if (rs.next()) {
                return rs.getString("value");
            }
            return null;
        });
    }

    public void setOption(String key, String value) {
        if(value != null) {
            getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
                PreparedStatement pstmt = con.prepareStatement("INSERT INTO options (key, value)\n" +
                        "VALUES (?, ?)\n" +
                        "ON CONFLICT (key) DO UPDATE\n" +
                        "    SET value = excluded.value");
                pstmt.setString(1, key);
                pstmt.setString(2, value);

                pstmt.executeUpdate();
                return null;
            });
        } else {
            delOption(key, false);
        }
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
