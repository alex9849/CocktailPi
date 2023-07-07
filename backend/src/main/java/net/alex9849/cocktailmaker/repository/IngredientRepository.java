package net.alex9849.cocktailmaker.repository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailmaker.model.recipe.ingredient.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class IngredientRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public List<Ingredient> findByIds(Long... ids) {
        if(ids.length == 0) {
            return new ArrayList<>();
        }

        return getJdbcTemplate().execute((ConnectionCallback<List<Ingredient>>) con -> {
            String stmt = "SELECT * FROM ingredients i WHERE i.id IN (";
            stmt += String.join(",", Arrays.stream(ids).map(x -> "?").collect(Collectors.toList()));
            stmt += ") order by i.name";

            PreparedStatement pstmt = con.prepareStatement(stmt);
            for (int i = 0; i < ids.length; i++) {
                pstmt.setLong(i + 1, ids[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            List<Ingredient> results = new ArrayList<>();
            while (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        });
    }

    public Optional<Ingredient> findById(long id) {
        List<Ingredient> foundList = findByIds(id);
        if(foundList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(foundList.get(0));
    }

    public Set<Ingredient> findGroupChildren(long id) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Ingredient>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM ingredients i WHERE i.parent_group_id = ?");
            pstmt.setLong(1, id);

            ResultSet rs = pstmt.executeQuery();
            Set<Ingredient> results = new HashSet<>();
            while (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        });
    }

    public Set<Long> findAddableIngredientsIdsInBar() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT i.id as id FROM ingredients i WHERE i.in_bar " +
                    "AND dType IN ('ManualIngredient', 'AutomatedIngredient')");
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> findIdsAutocompleteName(String toAutocomplete) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT i.id FROM ingredients i WHERE lower(name) LIKE ('%' || lower(?) || '%')");
            pstmt.setString(1, toAutocomplete);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    private static void setParameters(Ingredient ingredient, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, ingredient.getClass().getAnnotation(DiscriminatorValue.class).value());
        pstmt.setString(2, ingredient.getName());
        if(ingredient.getParentGroupId() != null) {
            pstmt.setLong(7, ingredient.getParentGroupId());
        } else {
            pstmt.setNull(7, Types.BIGINT);
        }

        if(ingredient instanceof AddableIngredient) {
            AddableIngredient aIngredient = (AddableIngredient) ingredient;
            pstmt.setInt(3, aIngredient.getAlcoholContent());
            pstmt.setBoolean(6, aIngredient.isInBar());
        } else {
            pstmt.setNull(3, Types.INTEGER);
            pstmt.setNull(6, Types.BOOLEAN);
        }
        if(ingredient instanceof ManualIngredient) {
            pstmt.setString(4, ingredient.getUnit().toString());
        } else {
            pstmt.setNull(4, Types.VARCHAR);
        }
        if(ingredient instanceof AutomatedIngredient) {
            pstmt.setDouble(5, ((AutomatedIngredient) ingredient).getPumpTimeMultiplier());
        } else {
            pstmt.setNull(5, Types.DOUBLE);
        }
    }

    public Set<Long> findIdsNotManual() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT i.id FROM ingredients i WHERE dtype != 'ManualIngredient'");
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> findIdsNotAutomatic() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT i.id FROM ingredients i WHERE dtype != 'AutomatedIngredient'");
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> findIdsNotGroup() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT i.id FROM ingredients i WHERE dtype != 'IngredientGroup'");
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }


    public Set<Long> findIdsWithoutParents() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT i.id FROM ingredients i WHERE i.parent_group_id IS NULL");
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> findGroupChildrenIds(long groupChildrenGroupId) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT aid.child as id FROM all_ingredient_dependencies aid WHERE aid.is_a = ?");
            pstmt.setLong(1, groupChildrenGroupId);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Optional<Ingredient> findByNameIgnoringCase(String name) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<Ingredient>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM ingredients i WHERE lower(name) = lower(?) ORDER BY name");
            pstmt.setString(1, name);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            while (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        });
    }

    public Ingredient create(Ingredient ingredient) {
        return getJdbcTemplate().execute((ConnectionCallback<Ingredient>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO ingredients (dtype, name, alcohol_content, " +
                    "unit, pump_time_multiplier, in_bar, parent_group_id) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            setParameters(ingredient, pstmt);
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return findById(rs.getLong(1)).orElse(null);
            }
            throw new IllegalStateException("Error saving ingredient");
        });
    }

    public boolean update(Ingredient ingredient) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("UPDATE ingredients SET dtype = ?, name = ?, alcohol_content = ?, " +
                    "unit = ?, pump_time_multiplier = ?, in_bar = ?, parent_group_id = ? WHERE id = ?");
            setParameters(ingredient, pstmt);
            pstmt.setLong(8, ingredient.getId());
            return pstmt.executeUpdate() != 0;
        });
    }

    public boolean delete(long id) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE from ingredients WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        });
    }

    private Ingredient parseRs(ResultSet resultSet) throws SQLException {
        Ingredient ingredient;
        String dType = resultSet.getString("dType");

        if(Objects.equals(dType, "ManualIngredient")) {
            ManualIngredient mIngredient = new ManualIngredient();
            mIngredient.setUnit(Ingredient.Unit.valueOf(resultSet.getString("unit")));
            mIngredient.setAlcoholContent(resultSet.getInt("alcohol_content"));
            mIngredient.setInBar(resultSet.getBoolean("in_bar"));
            ingredient = mIngredient;
        } else if(Objects.equals(dType, "AutomatedIngredient")) {
            AutomatedIngredient aIngredient = new AutomatedIngredient();
            aIngredient.setPumpTimeMultiplier(resultSet.getDouble("pump_time_multiplier"));
            aIngredient.setAlcoholContent(resultSet.getInt("alcohol_content"));
            aIngredient.setInBar(resultSet.getBoolean("in_bar"));
            ingredient = aIngredient;
        } else if (Objects.equals(dType, "IngredientGroup")) {
            ingredient = new IngredientGroup();
        } else {
            throw new IllegalArgumentException("IngredientType doesn't exist: " + dType);
        }
        ingredient.setId(resultSet.getLong("id"));
        ingredient.setParentGroupId(resultSet.getLong("parent_group_id"));
        if(resultSet.wasNull()) {
            ingredient.setParentGroupId(null);
        }
        ingredient.setName(resultSet.getString("name"));
        return ingredient;
    }

    public List<Ingredient> findAll() {
        return getJdbcTemplate().execute((ConnectionCallback<List<Ingredient>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM ingredients order by name");
            ResultSet rs = pstmt.executeQuery();
            List<Ingredient> results = new ArrayList<>();
            while (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        });
    }
}
