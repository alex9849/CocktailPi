package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeRepository {
    private final DataSource dataSource;

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    public RecipeRepository(DataSource dataSource) {
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

    public Optional<Recipe> findById(long id) {
        List<Recipe> foundList = this.findByIds(0, 1, RecipeOrderByField.NAME, Sort.Direction.ASC, id);
        if(foundList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(foundList.get(0));
    }

    public List<Recipe> findByIds(long offset, long limit, RecipeOrderByField orderBy, Sort.Direction sortDirection, Long... ids) {
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
                    recipeIngredientRepository.create(ri);
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
            recipeIngredientRepository.deleteByRecipe(recipe.getId());
            for(RecipeIngredient ri : recipe.getRecipeIngredients()) {
                ri.setRecipeId(recipe.getId());
                recipeIngredientRepository.create(ri);
            }
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error updating recipe", throwables);
        }
    }

    public boolean delete(long id) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("DELETE from recipes WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error deleting recipe", throwables);
        }
    }

    public Set<Long> getIdsInCategory(long categoryId) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT r.id AS id FROM recipes r " +
                    "JOIN recipe_categories rc on r.id = rc.recipe_id WHERE rc.categories_id = ?");
            pstmt.setLong(1, categoryId);
            return getIds(pstmt);
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }

    }

    public Set<Long> getIdsContainingName(String name) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes where lower(name) LIKE '%' | lower(?) | '%'");
            pstmt.setString(1, name);
            return getIds(pstmt);
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }

    }

    public Set<Long> getIdsInPublic() {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes where in_public = true");
            return getIds(pstmt);
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }

    }

    public Set<Long> getIdsByOwnerId(long id) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes where owner_id = ?");
            pstmt.setLong(1 , id);
            return getIds(pstmt);
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }

    }

    /**
     * @param pstmt needs to produce a resultset with exactly one numeric attribute
     * @return a parsed set of longs
     */
    private Set<Long> getIds(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();
        Set<Long> results = new HashSet<>();
        if (rs.next()) {
            results.add(rs.getLong(1));
        }
        return results;
    }

    private Recipe populateEntity(Recipe recipe) {
        recipe.setRecipeIngredients(recipeIngredientRepository.loadByRecipeId(recipe.getId()));
        for(RecipeIngredient ri : recipe.getRecipeIngredients()) {
            //Always non null, because of DB constraints
            ri.setIngredient(ingredientRepository.findById(ri.getIngredientId()).get());
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
