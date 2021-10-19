package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.AddIngredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
class RecipeIngredientRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public List<AddIngredient> loadByRecipeId(long recipeId) {
        return getJdbcTemplate().execute((ConnectionCallback<List<AddIngredient>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM recipe_ingredients where recipe_id = ?");
            pstmt.setLong(1, recipeId);
            ResultSet rs = pstmt.executeQuery();
            List<AddIngredient> results = new ArrayList<>();
            while (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        });
    }

    public boolean deleteByRecipe(long recipeId) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM recipe_ingredients WHERE recipe_id = ?");
            pstmt.setLong(1, recipeId);
            return pstmt.executeUpdate() != 0;
        });
    }

    public AddIngredient create(AddIngredient recipeIngredient) {
        return getJdbcTemplate().execute((ConnectionCallback<AddIngredient>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO recipe_ingredients " +
                    "(ingredient_id, recipe_id, production_step, amount, scale) VALUES (?, ?, ?, ?, ?)");
            pstmt.setLong(1, recipeIngredient.getIngredientId());
            pstmt.setLong(2, recipeIngredient.getRecipeId());
            pstmt.setLong(3, recipeIngredient.getProductionStep());
            pstmt.setLong(4, recipeIngredient.getAmount());
            pstmt.setBoolean(5, recipeIngredient.isScale());

            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            return recipeIngredient;
        });
    }

    private AddIngredient parseRs(ResultSet rs) throws SQLException {
        AddIngredient recipeIngredient = new AddIngredient();
        recipeIngredient.setAmount(rs.getInt("amount"));
        recipeIngredient.setRecipeId(rs.getLong("recipe_id"));
        recipeIngredient.setIngredientId(rs.getLong("ingredient_id"));
        recipeIngredient.setProductionStep(rs.getLong("production_step"));
        recipeIngredient.setScale(rs.getBoolean("scale"));
        return recipeIngredient;
    }

}
