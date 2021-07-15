package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ManualIngredient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import javax.persistence.DiscriminatorValue;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class IngredientRepository {
    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<Ingredient> findById(long id) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM ingredients WHERE id = ?");
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            List<Ingredient> results = new ArrayList<>();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading ingredient", throwables);
        }
    }

    public List<Ingredient> findOwnedByUser(long userId) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT i.* FROM ingredients i JOIN " +
                    "user_owned_ingredients uo ON uo.ingredient_id = i.id WHERE uo.user_id = ?");
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            List<Ingredient> results = new ArrayList<>();
            if (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading ingredient", throwables);
        }
    }

    public List<Ingredient> findAll() {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM ingredients");
            ResultSet rs = pstmt.executeQuery();
            List<Ingredient> results = new ArrayList<>();
            if (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading ingredient", throwables);
        }
    }

    public Optional<Ingredient> findByNameIgnoringCase(String name) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM ingredients WHERE lower(name) = lower(?)");
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading ingredient", throwables);
        }
    }

    public Ingredient create(Ingredient ingredient) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO ingredients (dtype, name, alcolhol_content, " +
                    "unit, pump_time_multiplier, add_to_volume) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
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
                pstmt.setBoolean(6, ((ManualIngredient) ingredient).isAddToVolume());
            } else {
                pstmt.setNull(6, Types.BOOLEAN);
            }
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                ingredient.setId(rs.getLong(1));
                return ingredient;
            }
            throw new IllegalStateException("Error saving ingredient");
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error saving ingredient", throwables);
        }
    }

    public boolean update(Ingredient ingredient) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("UPDATE ingredients SET dtype = ?, name = ?, alcolhol_content = ?, " +
                    "unit = ?, pump_time_multiplier = ?, add_to_volume = ? WHERE id = ?");
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
                pstmt.setBoolean(6, ((ManualIngredient) ingredient).isAddToVolume());
            } else {
                pstmt.setNull(5, Types.BOOLEAN);
            }
            pstmt.setLong(7, ingredient.getId());
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error updating ingredient", throwables);
        }
    }

    public boolean delete(long id) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("DELETE from ingredients WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error deleting ingredient", throwables);
        }
    }

    private Ingredient parseRs(ResultSet resultSet) throws SQLException {
        Ingredient ingredient;
        String dType = resultSet.getString("dType");
        if(Objects.equals(dType, "manual")) {
            ManualIngredient mIngredient = new ManualIngredient();
            mIngredient.setUnit(Ingredient.Unit.valueOf(resultSet.getString("unit")));
            ingredient = mIngredient;
        } else if(Objects.equals(dType, "automated")) {
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
