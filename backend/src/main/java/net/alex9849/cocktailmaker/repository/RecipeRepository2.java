package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeRepository2 {
    private final DataSource dataSource;

    @Autowired
    private RecipeIngredientRepository2 recipeIngredientRepository2;

    @Autowired
    private IngredientRepository2 ingredientRepository2;

    public RecipeRepository2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long count() {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT count(*) as number FROM recipes");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("number");
            }
            throw new IllegalStateException("Error counting recipes");
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error counting recipes", throwables);
        }
    }

    public List<Recipe> findAll() {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM recipes");
            ResultSet rs = pstmt.executeQuery();
            List<Recipe> results = new ArrayList<>();
            if (rs.next()) {
                results.add(parseRs(rs));
            }
            return results.stream().map(this::populateEntity).collect(Collectors.toList());
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }
    }

    public List<Recipe> findRecipesByIds(long offset, long limit, RecipeOrderByField orderBy, Sort.Direction sortDirection, Long... ids) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(String.format("SELECT * FROM recipes where id IN ? ORDER BY %s %s LIMIT ? OFFSET ?", orderBy.toString().toLowerCase(), sortDirection));
            pstmt.setArray(1, con.createArrayOf("BIGSERIAL", ids));
            pstmt.setLong(2, limit);
            pstmt.setLong(3, offset);
            ResultSet rs = pstmt.executeQuery();
            List<Recipe> results = new ArrayList<>();
            if (rs.next()) {
                results.add(parseRs(rs));
            }
            return results.stream().map(this::populateEntity).collect(Collectors.toList());
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }
    }

    public Recipe create(Recipe recipe) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO recipes (name, description, image, in_public, last_update, owner_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getDescription());
            pstmt.setBytes(3, recipe.getImage());
            pstmt.setBoolean(4, recipe.isInPublic());
            pstmt.setDate(5, new Date(System.currentTimeMillis()));
            pstmt.setLong(6, recipe.getOwnerId());
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                recipe.setId(rs.getLong(1));
                for(RecipeIngredient ri : recipe.getRecipeIngredients()) {
                    ri.setRecipeId(recipe.getId());
                    recipeIngredientRepository2.create(ri);
                }
                return recipe;
            }
            throw new IllegalStateException("Error saving recipe");
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error saving recipe", throwables);
        }
    }

    public boolean update(Recipe recipe) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("UPDATE recipes SET name = ?, " +
                    "description = ?, image = ?, in_public = ?, last_update = ?, owner_id = ? WHERE id = ?");
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getDescription());
            pstmt.setBytes(3, recipe.getImage());
            pstmt.setBoolean(4, recipe.isInPublic());
            pstmt.setDate(5, new Date(System.currentTimeMillis()));
            pstmt.setLong(6, recipe.getOwnerId());
            pstmt.setLong(7, recipe.getId());
            recipeIngredientRepository2.deleteByRecipe(recipe.getId());
            for(RecipeIngredient ri : recipe.getRecipeIngredients()) {
                ri.setRecipeId(recipe.getId());
                recipeIngredientRepository2.create(ri);
            }
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error updating recipe", throwables);
        }
    }

    private Recipe populateEntity(Recipe recipe) {
        recipe.setRecipeIngredients(recipeIngredientRepository2.loadByRecipeId(recipe.getId()));
        for(RecipeIngredient ri : recipe.getRecipeIngredients()) {
            //Always non null, because of DB constraints
            ri.setIngredient(ingredientRepository2.findById(ri.getIngredientId()).get());
        }
        return recipe;
    }

    private Recipe parseRs(ResultSet rs) throws SQLException {
        Recipe recipe = new Recipe();
        recipe.setOwnerId(rs.getLong("owner_id"));
        recipe.setId(rs.getLong("id"));
        recipe.setDescription(rs.getString("description"));
        recipe.setImage(rs.getBytes("image"));
        recipe.setName(rs.getString("name"));
        recipe.setInPublic(rs.getBoolean("in_public"));
        recipe.setLastUpdate(rs.getDate("last_update"));
        return recipe;
    }

    public enum RecipeOrderByField {
        AUTHOR, LAST_UPDATE, NAME
    }
}
