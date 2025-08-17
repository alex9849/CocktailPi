package net.alex9849.cocktailpi.repository;

import jakarta.annotation.PostConstruct;
import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.utils.SpringUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RecipeRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    @Autowired
    private ProductionStepRepository productionStepRepository;

    @Autowired
    private RecipeCategoryRepository recipeCategoryRepository;

    public long count() {
        Long ret = getJdbcTemplate().execute((ConnectionCallback<Long>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT count(*) as number FROM recipes");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("number");
            }
            throw new IllegalStateException("Error counting recipes");
        });
        return ret;
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

    public List<Recipe> findAll() {
        return getJdbcTemplate().execute((ConnectionCallback<List<Recipe>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT id, description, image IS NOT NULL AS has_image, name, owner_id, last_update, glass_id FROM recipes order by lower(name)");
            ResultSet rs = pstmt.executeQuery();
            List<Recipe> results = new ArrayList<>();
            while (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        });
    }

    public List<Recipe> findByIds(long offset, long limit, Sort sort, Long... ids) {
        return getJdbcTemplate().execute((ConnectionCallback<List<Recipe>>) con -> {
            StringBuilder sortSql = new StringBuilder();
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

            List<Object> params = new ArrayList<>();
            final String query;
            if (ids != null) {
                String idQuestionmarks = Arrays.stream(ids).map(x -> "?").collect(Collectors.joining(","));
                query = "SELECT id, description, image IS NOT NULL AS has_image, name, owner_id, last_update, glass_id FROM recipes where id IN (" + idQuestionmarks + ") " + sortSql + " LIMIT ? OFFSET ?";
                params.addAll(List.of(ids));
            } else {
                query = "SELECT id, description, image IS NOT NULL AS has_image, name, owner_id, last_update, glass_id FROM recipes " + sortSql + " LIMIT ? OFFSET ?";
            }
            params.add(limit);
            params.add(offset);

            PreparedStatement pstmt = con.prepareStatement(query);
            int paramIndex = 0;
            for (Object param : params) {
                pstmt.setObject(++paramIndex, param);
            }
            ResultSet rs = pstmt.executeQuery();
            List<Recipe> results = new ArrayList<>();
            while (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        });
    }

    public Recipe create(Recipe recipe) {
        return getJdbcTemplate().execute((ConnectionCallback<Recipe>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO recipes (name, normal_name, description, " +
                    "last_update, owner_id, glass_id) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getNormalName());
            if(recipe.getDescription() != null) {
                pstmt.setString(3, recipe.getDescription());
            } else {
                pstmt.setNull(3, Types.VARCHAR);
            }
            pstmt.setLong(4, recipe.getOwnerId());
            if(recipe.getDefaultGlass() != null) {
                pstmt.setLong(5, recipe.getDefaultGlass().getId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                recipe.setId(rs.getLong(1));
                productionStepRepository.create(recipe.getProductionSteps(), recipe.getId());
                for (Category category : recipe.getCategories()) {
                    recipeCategoryRepository.addToCategory(recipe.getId(), category.getId());
                }
                return recipe;
            }
            throw new IllegalStateException("Error saving recipe");
        });
    }

    public boolean update(Recipe recipe) {
        return Boolean.TRUE.equals(getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("UPDATE recipes SET name = ?, " +
                    "normal_name = ?, description = ?, last_update = CURRENT_TIMESTAMP, " +
                    "owner_id = ?, glass_id = ? WHERE id = ?");
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getNormalName());
            if (recipe.getDescription() != null) {
                pstmt.setString(3, recipe.getDescription());
            } else {
                pstmt.setNull(3, Types.VARCHAR);
            }
            pstmt.setLong(4, recipe.getOwnerId());
            if (recipe.getDefaultGlass() != null) {
                pstmt.setLong(5, recipe.getDefaultGlass().getId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.setLong(6, recipe.getId());
            productionStepRepository.deleteByRecipe(recipe.getId());
            productionStepRepository.create(recipe.getProductionSteps(), recipe.getId());
            recipeCategoryRepository.removeFromAllCategories(recipe.getId());
            for (Category category : recipe.getCategories()) {
                recipeCategoryRepository.addToCategory(recipe.getId(), category.getId());
            }
            return pstmt.executeUpdate() != 0;
        }));
    }

    public Set<Long> findIdsInCollection(long collectionId) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT r.id as id FROM collections c " +
                    "JOIN collection_recipes cr ON cr.collection_id = c.id " +
                    "JOIN recipes r ON cr.recipe_id = r.id " +
                    "WHERE c.id = ?");
            pstmt.setLong(1, collectionId);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public boolean delete(long id) {
        return Boolean.TRUE.equals(getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE from recipes WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        }));
    }

    public Set<Long> getIdsInCategory(long categoryId) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT r.id AS id FROM recipes r " +
                    "JOIN recipe_categories rc on r.id = rc.recipe_id WHERE rc.categories_id = ?");
            pstmt.setLong(1, categoryId);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> getIdsWithIngredients(Long... ingredientIds) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            String ingredientPlaceholders = String.join(", ", Arrays.stream(ingredientIds).map(x -> "?").toArray(String[]::new));

            PreparedStatement pstmt = con.prepareStatement("SELECT r.id\n" +
                    "FROM recipes r\n" +
                    "         join production_steps ps on ps.recipe_id = r.id\n" +
                    "         join production_step_ingredients psi on psi.recipe_id = ps.recipe_id and psi.\"order\" = ps.\"order\"\n" +
                    "         join ingredients i on i.id = psi.ingredient_id\n" +
                    "         join all_ingredient_dependencies id on i.id = id.child and id.is_a IN (" + ingredientPlaceholders + ")\n" +
                    "group by r.id\n" +
                    "having count(distinct id.is_a) == ?;");
            int currentIndex = 1;
            for(Long id : ingredientIds) {
                pstmt.setLong(currentIndex++, id);
            }
            pstmt.setInt(currentIndex++, ingredientIds.length);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> getIdsContainingName(String name) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes where lower(normal_name) LIKE ('%' || lower(?) || '%') or lower(name) LIKE ('%' || lower(?) || '%')");
            pstmt.setString(1, SpringUtility.normalize(name));
            pstmt.setString(2, name);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> getIdsByOwnerId(long id) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes where owner_id = ?");
            pstmt.setLong(1, id);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> getIdsByName(String name) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes where lower(normal_name) = lower(?) or lower(name) = lower(?)");
            pstmt.setString(1, SpringUtility.normalize(name));
            pstmt.setString(2, name);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Optional<byte[]> getImage(long recipeId) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<byte[]>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT image FROM recipes where id = ?");
            pstmt.setLong(1, recipeId);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSet.getBytes("image"));
            }
            return Optional.empty();
        });
    }

    public void setImage(long recipeId, byte[] image) {
        getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
            if (image == null) {
                PreparedStatement deleteImagePstmt = con.prepareStatement("UPDATE recipes SET image = NULL, last_update = CURRENT_TIMESTAMP where id = ?");
                deleteImagePstmt.setLong(1, recipeId);
                deleteImagePstmt.executeUpdate();
                return null;
            }
            PreparedStatement updateLobOidPstmt = con.prepareStatement("UPDATE recipes SET image = ?, last_update = CURRENT_TIMESTAMP where id = ?");
            updateLobOidPstmt.setBytes(1, image);
            updateLobOidPstmt.setLong(2, recipeId);
            updateLobOidPstmt.executeUpdate();
            return null;
        });
    }

    public Set<Long> getIdsOfFullyAutomaticallyFabricableRecipes() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT r.id\n" +
                    "FROM recipes r\n" +
                    "         left join production_steps ps on ps.recipe_id = r.id\n" +
                    "         left join production_step_ingredients psi on psi.recipe_id = ps.recipe_id and psi.\"order\" = ps.\"order\"\n" +
                    "         left join ingredients i on i.id = psi.ingredient_id\n" +
                    "group by r.id\n" +
                    "having count(i.id) == sum(\n" +
                    "        EXISTS(\n" +
                    "                SELECT ide.leaf\n" +
                    "                FROM concrete_ingredient_dependencies ide\n" +
                    "                         JOIN ingredients i_sub ON i_sub.id = ide.leaf\n" +
                    "                         JOIN pumps p ON i_sub.id = p.current_ingredient_id AND i_sub.dtype = 'AutomatedIngredient'\n" +
                    "                WHERE ide.is_a = i.id\n" +
                    "            )\n" +
                    "    )\n");
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> getIdsOfRecipesWithAllIngredientsInBarOrOnPumps() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT r.id\n" +
                            "FROM recipes r\n" +
                            "         left join production_steps ps on ps.recipe_id = r.id\n" +
                            "         left join production_step_ingredients psi on psi.recipe_id = ps.recipe_id and psi.\"order\" = ps.\"order\"\n" +
                            "         left join ingredients i\n" +
                            "                   on i.id = psi.ingredient_id\n" +
                            "group by r.id\n" +
                            "having count(i.id) == sum(\n" +
                            "            EXISTS (SELECT ide.leaf\n" +
                            "                    FROM concrete_ingredient_dependencies ide\n" +
                            "                             JOIN ingredients i_sub ON i_sub.id = ide.leaf\n" +
                            "                    WHERE ide.is_a = i.id\n" +
                            "                      AND i_sub.dtype != 'IngredientGroup'\n" +
                            "                      AND i_sub.in_bar)\n" +
                            "            OR EXISTS (SELECT ide.leaf\n" +
                            "                       FROM concrete_ingredient_dependencies ide\n" +
                            "                                JOIN ingredients i_sub ON i_sub.id = ide.leaf\n" +
                            "                                JOIN pumps p ON i_sub.id = p.current_ingredient_id\n" +
                            "                           AND i_sub.dtype = 'AutomatedIngredient'\n" +
                            "                       WHERE ide.is_a = i.id)\n" +
                            "    )");
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    private Recipe parseRs(ResultSet rs) throws SQLException {
        Recipe recipe = new Recipe();
        recipe.setOwnerId(rs.getLong("owner_id"));
        recipe.setId(rs.getLong("id"));
        recipe.setDescription(rs.getString("description"));
        recipe.setName(rs.getString("name"));
        recipe.setLastUpdate(rs.getTimestamp("last_update"));
        recipe.setHasImage(rs.getBoolean("has_image"));
        recipe.setDefaultGlassId(rs.getLong("glass_id"));
        return recipe;
    }
}
