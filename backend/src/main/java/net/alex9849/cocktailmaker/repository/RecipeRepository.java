package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ServerErrorException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class RecipeRepository {
    private final DataSource dataSource;

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    public RecipeRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long count() {
        try (Connection con = dataSource.getConnection()) {
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

    public Optional<Recipe> findById(long id) {
        List<Recipe> foundList = this.findByIds(0, 1, Sort.by(Sort.Direction.ASC, "name"), id);
        if (foundList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(foundList.get(0));
    }

    public List<Recipe> findAll(long offset, long limit, Sort sort) {
        return this.findByIds(offset, limit, sort, null);
    }

    public List<Recipe> findByIds(long offset, long limit, Sort sort, Long... ids) {
        StringBuilder sortSql = new StringBuilder("");
        boolean isSortFirst = true;
        for (Sort.Order order : sort) {
            if (isSortFirst) {
                isSortFirst = false;
                sortSql.append("ORDER BY ")
                        .append(order.getProperty())
                        .append(" ")
                        .append(order.getDirection().name());
            } else {
                sortSql.append(", ")
                        .append(order.getProperty())
                        .append(" ")
                        .append(order.getDirection().name());
            }
        }
        try (Connection con = dataSource.getConnection()) {
            List<Object> params = new ArrayList<>();
            final String query;
            if (ids != null) {
                query = "SELECT * FROM recipes where id = ANY(?) " + sortSql + " LIMIT ? OFFSET ?";
                params.add(con.createArrayOf("int8", ids));
            } else {
                if(ids.length == 0) {
                    return new ArrayList<>();
                }
                query = "SELECT * FROM recipes " + sortSql + " LIMIT ? OFFSET ?";
            }
            params.add(limit);
            params.add(offset);

            PreparedStatement pstmt = con.prepareStatement(query);
            int paramIndex = 0;
            for(Object param : params) {
                pstmt.setObject(++paramIndex, param);
            }
            ResultSet rs = pstmt.executeQuery();
            List<Recipe> results = new ArrayList<>();
            while (rs.next()) {
                results.add(parseRs(rs));
            }
            return results.stream().map(this::populateEntity).collect(Collectors.toList());
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }
    }

    public Recipe create(Recipe recipe) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO recipes (name, description, in_public, last_update, owner_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getDescription());
            pstmt.setBoolean(3, recipe.isInPublic());
            pstmt.setDate(4, new Date(System.currentTimeMillis()));
            pstmt.setLong(5, recipe.getOwnerId());
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                recipe.setId(rs.getLong(1));
                for (RecipeIngredient ri : recipe.getRecipeIngredients()) {
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
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("UPDATE recipes SET name = ?, " +
                    "description = ?, in_public = ?, last_update = ?, owner_id = ? WHERE id = ?");
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getDescription());
            pstmt.setBoolean(3, recipe.isInPublic());
            pstmt.setDate(4, new Date(System.currentTimeMillis()));
            pstmt.setLong(5, recipe.getOwnerId());
            pstmt.setLong(6, recipe.getId());
            recipeIngredientRepository.deleteByRecipe(recipe.getId());
            for (RecipeIngredient ri : recipe.getRecipeIngredients()) {
                ri.setRecipeId(recipe.getId());
                recipeIngredientRepository.create(ri);
            }
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error updating recipe", throwables);
        }
    }

    public boolean delete(long id) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("DELETE from recipes WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error deleting recipe", throwables);
        }
    }

    public Set<Long> getIdsInCategory(long categoryId) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT r.id AS id FROM recipes r " +
                    "JOIN recipe_categories rc on r.id = rc.recipe_id WHERE rc.categories_id = ?");
            pstmt.setLong(1, categoryId);
            return DbUtils.getIds(pstmt);
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }
    }

    public Set<Long> getIdsWithIngredients(Long... ingredientIds) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT r.id AS id FROM recipes r " +
                    "JOIN recipe_ingredients ri on r.id = ri.recipe_id " +
                    "WHERE ri.ingredient_id IN ?");
            pstmt.setArray(1, con.createArrayOf("BIGSERIAL", ingredientIds));
            return DbUtils.getIds(pstmt);
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }
    }

    public Set<Long> getIdsContainingName(String name) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes where lower(name) LIKE '%' | lower(?) | '%'");
            pstmt.setString(1, name);
            return DbUtils.getIds(pstmt);
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }

    }

    public Set<Long> getIdsInPublic() {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes where in_public = true");
            return DbUtils.getIds(pstmt);
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }

    }

    public Set<Long> getIdsByOwnerId(long id) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes where owner_id = ?");
            pstmt.setLong(1, id);
            return DbUtils.getIds(pstmt);
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }

    }

    public Optional<byte[]> getImage(long recipeId) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT image FROM recipes where id = ?");
            pstmt.setLong(1, recipeId);
            ResultSet resultSet = pstmt.executeQuery();
            if(resultSet.next()) {
                Long imageOid = resultSet.getObject("image", Long.class);
                if(imageOid == null) {
                    return Optional.empty();
                }
                con.setAutoCommit(false);
                LargeObjectManager lobApi = con.unwrap(PGConnection.class).getLargeObjectAPI();
                LargeObject imageLob = lobApi.open(imageOid, LargeObjectManager.READ);
                byte buf[] = new byte[imageLob.size()];
                imageLob.read(buf, 0 , buf.length);
                return Optional.of(buf);
            }
            return Optional.empty();
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe image", throwables);
        }
    }

    public boolean setImage(long recipeId, byte[] image) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("UPDATE recipes SET image = ? where id = ?");
            pstmt.setBytes(1, image);
            pstmt.setLong(2, recipeId);
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe image", throwables);
        }
    }

    public Set<Long> getIdsOfFabricableRecipes() {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes r " +
                    "join recipe_ingredients ri on r.id = ri.recipe_id " +
                    "join ingredients i on i.id = ri.ingredient_id AND i.dtype = 'AutomatedIngredient'" +
                    "left join pumps p on i.id = p.current_ingredient_id " +
                    "group by r.id having (count(*) - count(p.id)) = 0");
            return DbUtils.getIds(pstmt);
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }
    }

    public Set<Long> getIdsOfRecipesWithAllIngredientsInBar(long userId) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes r " +
                    "join recipe_ingredients ri on r.id = ri.recipe_id " +
                    "left join user_owned_ingredients uoi on ri.ingredient_id = uoi.ingredient_id AND uoi.user_id = ? " +
                    "group by r.id having (count(*) - count(uoi.ingredient_id)) = 0");
            pstmt.setLong(1, userId);
            return DbUtils.getIds(pstmt);
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading recipe", throwables);
        }
    }

    private Recipe populateEntity(Recipe recipe) {
        recipe.setRecipeIngredients(recipeIngredientRepository.loadByRecipeId(recipe.getId()));
        for (RecipeIngredient ri : recipe.getRecipeIngredients()) {
            //Always non null, because of DB constraints
            ri.setIngredient(ingredientRepository.findById(ri.getIngredientId()).get());
        }
        recipe.setOwner(userRepository.findById(recipe.getOwnerId()).get());
        recipe.setCategories(categoryRepository.findByRecipeId(recipe.getId()));
        return recipe;
    }

    private Recipe parseRs(ResultSet rs) throws SQLException {
        Recipe recipe = new Recipe();
        recipe.setOwnerId(rs.getLong("owner_id"));
        recipe.setId(rs.getLong("id"));
        recipe.setDescription(rs.getString("description"));
        recipe.setName(rs.getString("name"));
        recipe.setInPublic(rs.getBoolean("in_public"));
        recipe.setLastUpdate(rs.getDate("last_update"));
        return recipe;
    }
}
