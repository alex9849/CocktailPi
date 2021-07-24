package net.alex9849.cocktailmaker.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.server.ServerErrorException;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class UserOwnedRecipeIngredientRepository {
    private final DataSource dataSource;

    public UserOwnedRecipeIngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean ownsIngredient(long userId, long ingredientId) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM user_owned_ingredients " +
                    "WHERE ingredient_id = ? AND user_id = ?");
            pstmt.setLong(1, ingredientId);
            pstmt.setLong(2, userId);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            return rs.next();
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading user_owned_ingredient", throwables);
        }
    }

    public boolean delete(long userId, long ingredientId) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM user_owned_ingredients WHERE " +
                    "ingredient_id = ? AND user_id = ?");
            pstmt.setLong(1, ingredientId);
            pstmt.setLong(2, userId);
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error deleting user_owned_ingredient", throwables);
        }
    }

    public void create(long userId, long ingredientId) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO user_owned_ingredients " +
                    "(user_id, ingredient_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, userId);
            pstmt.setLong(2, ingredientId);
            pstmt.execute();
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error saving user_owned_ingredient", throwables);
        }
    }

}
