package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.ProductionStepIngredient;
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
public class ProductionStepIngredientRepository extends JdbcDaoSupport {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private IngredientRepository ingredientRepository;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }


    public List<ProductionStepIngredient> loadProductionStepIngredients(long recipeId, int orderIndex) {
        return getJdbcTemplate().execute((ConnectionCallback<List<ProductionStepIngredient>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM production_step_ingredients where recipe_id = ? " +
                    "AND \"order\" = ? order by amount desc");
            pstmt.setLong(1, recipeId);
            pstmt.setInt(2, orderIndex);
            ResultSet rs = pstmt.executeQuery();
            List<ProductionStepIngredient> results = new ArrayList<>();
            while (rs.next()) {
                results.add(populateEntity(parseRs(rs), rs.getLong("ingredient_id")));
            }
            return results;
        });
    }

    public List<ProductionStepIngredient> create(List<ProductionStepIngredient> stepIngredients, long recipeId, int orderIndex) {
        if(stepIngredients.isEmpty()) {
            return stepIngredients;
        }
        return getJdbcTemplate().execute((ConnectionCallback<List<ProductionStepIngredient>>) con -> {
            String sqlQuery = "INSERT INTO production_step_ingredients (recipe_id, \"order\", ingredient_id, amount, scale) VALUES";
            for(ProductionStepIngredient psi : stepIngredients) {
                sqlQuery += " (?, ?, ?, ?, ?)";
            }

            PreparedStatement pstmt = con.prepareStatement(sqlQuery);
            final int paramSize = 5;
            for(int i = 0; i < stepIngredients.size(); i++) {
                ProductionStepIngredient currentPsi = stepIngredients.get(i);
                pstmt.setLong(paramSize * i + 1, recipeId);
                pstmt.setInt(paramSize * i + 2, orderIndex);
                pstmt.setLong(paramSize * i + 3, currentPsi.getIngredient().getId());
                pstmt.setInt(paramSize * i + 4, currentPsi.getAmount());
                pstmt.setBoolean(paramSize * i + 5, currentPsi.isScale());
            }

            pstmt.execute();
            return stepIngredients;
        });
    }

    private ProductionStepIngredient populateEntity(ProductionStepIngredient ps, long ingredientId) {
        ps.setIngredient(ingredientRepository.findById(ingredientId).orElse(null));
        return ps;
    }

    private ProductionStepIngredient parseRs(ResultSet rs) throws SQLException {
        ProductionStepIngredient psi = new ProductionStepIngredient();
        psi.setAmount(rs.getInt("amount"));
        psi.setScale(rs.getBoolean("scale"));
        return psi;
    }
}