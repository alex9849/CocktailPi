package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.Category;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
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
    private UserRepository userRepository;

    @Autowired
    private RecipeCategoryRepository recipeCategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    public long count() {
        return getJdbcTemplate().execute((ConnectionCallback<Long>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT count(*) as number FROM recipes");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("number");
            }
            throw new IllegalStateException("Error counting recipes");
        });
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
        return getJdbcTemplate().execute((ConnectionCallback<List<Recipe>>) con -> {
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
        });
    }

    public Recipe create(Recipe recipe) {
        return getJdbcTemplate().execute((ConnectionCallback<Recipe>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO recipes (name, description, in_public, last_update, " +
                    "owner_id, default_amount_to_fill) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getDescription());
            pstmt.setBoolean(3, recipe.isInPublic());
            pstmt.setDate(4, new Date(System.currentTimeMillis()));
            pstmt.setLong(5, recipe.getOwnerId());
            pstmt.setLong(6, recipe.getDefaultAmountToFill());
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                recipe.setId(rs.getLong(1));
                productionStepRepository.create(recipe.getProductionSteps(), recipe.getId());
                for(Category category : recipe.getCategories()) {
                    recipeCategoryRepository.addToCategory(recipe.getId(), category.getId());
                }
                return recipe;
            }
            throw new IllegalStateException("Error saving recipe");
        });
    }

    public boolean update(Recipe recipe) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("UPDATE recipes SET name = ?, " +
                    "description = ?, in_public = ?, last_update = ?, owner_id = ?, " +
                    "default_amount_to_fill = ? WHERE id = ?");
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getDescription());
            pstmt.setBoolean(3, recipe.isInPublic());
            pstmt.setDate(4, new Date(System.currentTimeMillis()));
            pstmt.setLong(5, recipe.getOwnerId());
            pstmt.setLong(6, recipe.getDefaultAmountToFill());
            pstmt.setLong(7, recipe.getId());
            productionStepRepository.deleteByRecipe(recipe.getId());
            productionStepRepository.create(recipe.getProductionSteps(), recipe.getId());
            recipeCategoryRepository.removeFromAllCategories(recipe.getId());
            for(Category category : recipe.getCategories()) {
                recipeCategoryRepository.addToCategory(recipe.getId(), category.getId());
            }
            return pstmt.executeUpdate() != 0;
        });
    }

    public Set<Long> findIdsInCollection(long collectionId) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT r.id as id FROM collections c " +
                    "JOIN collection_recipes cr ON cr.collection_id = c.id " +
                    "JOIN recipes r ON cr.recipe_id = r.id AND (r.in_public OR r.owner_id = c.owner_id) " +
                    "WHERE c.id = ?");
            pstmt.setLong(1, collectionId);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public boolean delete(long id) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE from recipes WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        });
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
            PreparedStatement pstmt = con.prepareStatement("SELECT r.id\n" +
                    "FROM recipes r\n" +
                    "         join recipe_ingredients ri on r.id = ri.recipe_id\n" +
                    "         join ingredients i on i.id = ri.ingredient_id\n" +
                    "         join unnest(?) requiredIngredientId on requiredIngredientId = i.id\n" +
                    "group by r.id\n" +
                    "having count(distinct i.id) = cardinality(?)");
            pstmt.setArray(1, con.createArrayOf("int8", ingredientIds));
            pstmt.setArray(2, con.createArrayOf("int8", ingredientIds));
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> getIdsContainingName(String name) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes where lower(name) LIKE ('%' || lower(?) || '%')");
            pstmt.setString(1, name);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> getIdsInPublic() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes where in_public = true");
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

    public Set<Long> getIdsPermittedForUserId(Long userId) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT id AS id FROM recipes WHERE owner_id = ? OR in_public");
            pstmt.setLong(1, userId);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Optional<byte[]> getImage(long recipeId) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<byte[]>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT image FROM recipes where id = ?");
            pstmt.setLong(1, recipeId);
            ResultSet resultSet = pstmt.executeQuery();
            if(resultSet.next()) {
                Long imageOid = resultSet.getObject("image", Long.class);
                if(imageOid == null) {
                    return Optional.empty();
                }
                LargeObjectManager lobApi = con.unwrap(PGConnection.class).getLargeObjectAPI();
                LargeObject imageLob = lobApi.open(imageOid, LargeObjectManager.READ);
                byte buf[] = new byte[imageLob.size()];
                imageLob.read(buf, 0 , buf.length);
                return Optional.of(buf);
            }
            return Optional.empty();
        });
    }

    public void setImage(long recipeId, byte[] image) {
        getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT image FROM recipes where id = ? AND image IS NOT NULL");
            pstmt.setLong(1, recipeId);
            ResultSet resultSet = pstmt.executeQuery();
            LargeObjectManager lobApi = con.unwrap(PGConnection.class).getLargeObjectAPI();
            Long imageOid;
            if(resultSet.next()) {
                imageOid = resultSet.getObject("image", Long.class);
            } else {
                imageOid = lobApi.createLO(LargeObjectManager.READWRITE);
            }
            if(image == null) {
                PreparedStatement deleteImagePstmt = con.prepareStatement("UPDATE recipes SET image = NULL where id = ?");
                deleteImagePstmt.setLong(1, recipeId);
                deleteImagePstmt.executeUpdate();
                lobApi.delete(imageOid);
                return null;
            }
            LargeObject lobObject = lobApi.open(imageOid, LargeObjectManager.READWRITE);
            lobObject.write(image);
            PreparedStatement updateLobOidPstmt = con.prepareStatement("UPDATE recipes SET image = ? where id = ?");
            updateLobOidPstmt.setLong(1, imageOid);
            updateLobOidPstmt.setLong(2, recipeId);
            updateLobOidPstmt.executeUpdate();
            return null;
        });
    }

    public Set<Long> getIdsOfFullyAutomaticallyFabricableRecipes() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT r.id AS id\n" +
                    "FROM recipes r\n" +
                    "         join production_steps ps on ps.recipe_id = r.id\n" +
                    "         join production_step_ingredients psi on psi.recipe_id = ps.recipe_id and psi.\"order\" = ps.\"order\"\n" +
                    "         join ingredients i on i.id = psi.ingredient_id AND i.dtype = 'AutomatedIngredient'\n" +
                    "         left join pumps p on i.id = p.current_ingredient_id\n" +
                    "group by r.id\n" +
                    "having count(*) = count(p.id)");
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> getIdsOfRecipesWithAllIngredientsOwnedOrOnPumps(long userId) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT recipeId\n" +
                            "from (SELECT r.id           AS recipeId,\n" +
                            "             CASE\n" +
                            "                 WHEN p.current_ingredient_id IS NOT NULL\n" +
                            "                     OR uoi.ingredient_id IS NOT NULL THEN 1\n" +
                            "                 ELSE 0 END AS owned\n" +
                            "      FROM recipes r\n" +
                            "               join production_steps ps on ps.recipe_id = r.id\n" +
                            "               join production_step_ingredients psi on psi.recipe_id = ps.recipe_id and psi.\"order\" = ps.\"order\"\n" +
                            "               join ingredients i on i.id = psi.ingredient_id\n" +
                            "               left join pumps p on i.id = p.current_ingredient_id AND i.dtype = 'AutomatedIngredient'\n" +
                            "               left join user_owned_ingredients uoi on i.id = uoi.ingredient_id AND uoi.user_id = ?) as sub\n" +
                            "group by recipeId\n" +
                            "having count(*) - sum(owned) = 0");
            pstmt.setLong(1, userId);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    private Recipe populateEntity(Recipe recipe) {
        recipe.setProductionSteps(productionStepRepository.loadByRecipeId(recipe.getId()));
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
        recipe.setLastUpdate(rs.getTimestamp("last_update"));
        recipe.setDefaultAmountToFill(rs.getLong("default_amount_to_fill"));
        return recipe;
    }
}
