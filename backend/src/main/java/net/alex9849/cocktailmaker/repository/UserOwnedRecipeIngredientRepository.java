package net.alex9849.cocktailmaker.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Repository
public class UserOwnedRecipeIngredientRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public boolean ownsIngredient(long userId, long ingredientId) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM user_owned_ingredients " +
                    "WHERE ingredient_id = ? AND user_id = ?");
            pstmt.setLong(1, ingredientId);
            pstmt.setLong(2, userId);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            return rs.next();
        });
    }

    public boolean delete(long userId, long ingredientId) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM user_owned_ingredients WHERE " +
                    "ingredient_id = ? AND user_id = ?");
            pstmt.setLong(1, ingredientId);
            pstmt.setLong(2, userId);
            return pstmt.executeUpdate() != 0;
        });
    }

    public void create(long userId, long ingredientId) {
        getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO user_owned_ingredients " +
                    "(user_id, ingredient_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, userId);
            pstmt.setLong(2, ingredientId);
            pstmt.execute();
            return null;
        });
    }

}
