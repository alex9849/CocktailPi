package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.IngredientGroup;
import net.alex9849.cocktailmaker.model.recipe.ManualIngredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.persistence.DiscriminatorValue;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Repository
public class IngredientRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public List<IngredientGroup> findByIds(Long... ids) {
        if(ids.length == 0) {
            return new ArrayList<>();
        }
        return getJdbcTemplate().execute((ConnectionCallback<List<IngredientGroup>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM ingredients i " +
                    "WHERE i.id = ANY(?) order by i.name");

            pstmt.setArray(1, con.createArrayOf("int8", ids));
            ResultSet rs = pstmt.executeQuery();
            List<IngredientGroup> results = new ArrayList<>();
            while (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        });
    }

    public Optional<IngredientGroup> findById(long id) {
        List<IngredientGroup> foundList = findByIds(id);
        if(foundList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(foundList.get(0));
    }

    public List<IngredientGroup> findAll() {
        return getJdbcTemplate().execute((ConnectionCallback<List<IngredientGroup>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM ingredients order by name");
            ResultSet rs = pstmt.executeQuery();
            List<IngredientGroup> results = new ArrayList<>();
            while (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        });
    }

    //Todo ingredientgroups
    public Set<Long> findIdsInBar() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT i.id as id FROM ingredients i WHERE i.in_bar");
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

    public Set<Long> findIdsNotManual() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT i.id FROM ingredients i WHERE dtype != 'ManualIngredient'");
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Optional<IngredientGroup> findByNameIgnoringCase(String name) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<IngredientGroup>>) con -> {
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

    public IngredientGroup create(Ingredient ingredient) {
        return getJdbcTemplate().execute((ConnectionCallback<IngredientGroup>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO ingredients (dtype, name, alcohol_content, " +
                    "unit, pump_time_multiplier, in_bar) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, ingredient.getClass().getAnnotation(DiscriminatorValue.class).value());
            pstmt.setString(2, ingredient.getName());
            pstmt.setDouble(3, ingredient.getAlcoholContent());
            pstmt.setString(4, ingredient.getUnit().name());
            if(ingredient instanceof AutomatedIngredient) {
                pstmt.setDouble(5, ((AutomatedIngredient) ingredient).getPumpTimeMultiplier());
            } else {
                pstmt.setNull(5, Types.DOUBLE);
            }
            pstmt.setBoolean(6, ingredient.isInBar());
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                ingredient.setId(rs.getLong(1));
                return ingredient;
            }
            throw new IllegalStateException("Error saving ingredient");
        });
    }

    public boolean update(Ingredient ingredient) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("UPDATE ingredients SET dtype = ?, name = ?, alcohol_content = ?, " +
                    "unit = ?, pump_time_multiplier = ?, in_bar = ? WHERE id = ?");
            pstmt.setString(1, ingredient.getClass().getAnnotation(DiscriminatorValue.class).value());
            pstmt.setString(2, ingredient.getName());
            pstmt.setString(4, ingredient.getUnit().toString());
            if(ingredient instanceof AutomatedIngredient) {
                pstmt.setDouble(5, ((AutomatedIngredient) ingredient).getPumpTimeMultiplier());
            } else {
                pstmt.setNull(5, Types.DOUBLE);
            }
            if(ingredient instanceof Ingredient) {
                pstmt.setInt(3, ingredient.getAlcoholContent());
                pstmt.setBoolean(6, ingredient.isInBar());
            } else {
                pstmt.setNull(3, Types.INTEGER);
                pstmt.setNull(6, Types.BOOLEAN);
            }
            pstmt.setLong(7, ingredient.getId());
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

    private IngredientGroup parseRs(ResultSet resultSet) throws SQLException {
        IngredientGroup ingredient;
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
        ingredient.setName(resultSet.getString("name"));
        ingredient.setParentGroupId(resultSet.getLong("parent_group_id"));
        ingredient.setId(resultSet.getLong("id"));
        return ingredient;
    }
}
