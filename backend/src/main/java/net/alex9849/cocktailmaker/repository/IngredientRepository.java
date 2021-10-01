package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
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

    public List<Ingredient> findByIds(Long... ids) {
        if(ids.length == 0) {
            return new ArrayList<>();
        }
        return getJdbcTemplate().execute((ConnectionCallback<List<Ingredient>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM ingredients i " +
                    "WHERE i.id = ANY(?) order by i.name");

            pstmt.setArray(1, con.createArrayOf("int8", ids));
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

    public Set<Long> findIdsOwnedByUser(long userId) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT i.id as id FROM ingredients i JOIN " +
                    "user_owned_ingredients uo ON uo.ingredient_id = i.id WHERE uo.user_id = ?");
            pstmt.setLong(1, userId);
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
                    "unit, pump_time_multiplier, add_to_volume, scale_to_volume) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, ingredient.getClass().getAnnotation(DiscriminatorValue.class).value());
            pstmt.setString(2, ingredient.getName());
            pstmt.setDouble(3, ingredient.getAlcoholContent());
            pstmt.setString(4, ingredient.getUnit().name());
            if(ingredient instanceof AutomatedIngredient) {
                pstmt.setDouble(5, ((AutomatedIngredient) ingredient).getPumpTimeMultiplier());
            } else {
                pstmt.setNull(5, Types.DOUBLE);
            }
            if(ingredient instanceof ManualIngredient) {
                ManualIngredient manualIngredient = (ManualIngredient) ingredient;
                if(ingredient.getUnit() == Ingredient.Unit.MILLILITER) {
                    pstmt.setBoolean(6, manualIngredient.isAddToVolume());
                } else {
                    pstmt.setBoolean(6, false);
                }
                if(ingredient.getUnit() == Ingredient.Unit.MILLILITER) {
                    pstmt.setBoolean(7, false);
                } else {
                    pstmt.setBoolean(7, manualIngredient.isScaleToVolume());
                }
            } else {
                pstmt.setNull(6, Types.BOOLEAN);
                pstmt.setNull(7, Types.BOOLEAN);
            }
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
                    "unit = ?, pump_time_multiplier = ?, add_to_volume = ?, scale_to_volume = ? WHERE id = ?");
            pstmt.setString(1, ingredient.getClass().getAnnotation(DiscriminatorValue.class).value());
            pstmt.setString(2, ingredient.getName());
            pstmt.setDouble(3, ingredient.getAlcoholContent());
            pstmt.setString(4, ingredient.getUnit().toString());
            if(ingredient instanceof AutomatedIngredient) {
                pstmt.setDouble(5, ((AutomatedIngredient) ingredient).getPumpTimeMultiplier());
            } else {
                pstmt.setNull(5, Types.DOUBLE);
            }
            if(ingredient instanceof ManualIngredient) {
                ManualIngredient manualIngredient = (ManualIngredient) ingredient;
                if(ingredient.getUnit() == Ingredient.Unit.MILLILITER) {
                    pstmt.setBoolean(6, manualIngredient.isAddToVolume());
                } else {
                    pstmt.setBoolean(6, false);
                }
                if(ingredient.getUnit() == Ingredient.Unit.MILLILITER) {
                    pstmt.setBoolean(7, false);
                } else {
                    pstmt.setBoolean(7, manualIngredient.isScaleToVolume());
                }
            } else {
                pstmt.setNull(6, Types.BOOLEAN);
                pstmt.setNull(7, Types.BOOLEAN);
            }
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
            mIngredient.setAddToVolume(resultSet.getBoolean("add_to_volume"));
            mIngredient.setScaleToVolume(resultSet.getBoolean("scale_to_volume"));
            ingredient = mIngredient;
        } else if(Objects.equals(dType, "AutomatedIngredient")) {
            AutomatedIngredient aIngredient = new AutomatedIngredient();
            aIngredient.setPumpTimeMultiplier(resultSet.getDouble("pump_time_multiplier"));
            ingredient = aIngredient;
        } else {
            throw new IllegalArgumentException("IngredientType doesn't exist: " + dType);
        }
        ingredient.setName(resultSet.getString("name"));
        ingredient.setAlcoholContent(resultSet.getInt("alcohol_content"));
        ingredient.setId(resultSet.getLong("id"));
        return ingredient;
    }
}
