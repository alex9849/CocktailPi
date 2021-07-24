package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
class RecipeIngredientRepository {
    private final DataSource dataSource;

    public RecipeIngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<RecipeIngredient> loadByRecipeId(long recipeId) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM recipe_ingredients where recipe_id = ?");
            pstmt.setLong(1, recipeId);
            ResultSet rs = pstmt.executeQuery();
            List<RecipeIngredient> results = new ArrayList<>();
            while (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipeIngredient", throwables);
        }
    }

    public boolean deleteByRecipe(long recipeId) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM recipe_ingredients WHERE recipe_id = ?");
            pstmt.setLong(1, recipeId);
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error deleting recipeIngredients", throwables);
        }
    }

    public RecipeIngredient create(RecipeIngredient recipeIngredient) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO recipe_ingredients (ingredient_id, recipe_id, production_step, amount) " +
                    "VALUES (?, ?, ?, ?)");
            pstmt.setLong(1, recipeIngredient.getIngredientId());
            pstmt.setLong(2, recipeIngredient.getRecipeId());
            pstmt.setLong(3, recipeIngredient.getProductionStep());
            pstmt.setLong(4, recipeIngredient.getAmount());

            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            return recipeIngredient;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error saving recipeIngredient", throwables);
        }
    }

    private RecipeIngredient parseRs(ResultSet rs) throws SQLException {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setAmount(rs.getInt("amount"));
        recipeIngredient.setRecipeId(rs.getLong("recipe_id"));
        recipeIngredient.setIngredientId(rs.getLong("ingredient_id"));
        recipeIngredient.setProductionStep(rs.getLong("production_step"));
        return recipeIngredient;
    }

}
