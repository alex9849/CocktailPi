package net.alex9849.cocktailpi.repository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.WrittenInstructionProductionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class ProductionStepRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProductionStepIngredientRepository productionStepIngredientRepository;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public List<ProductionStep> loadByRecipeId(long recipeId) {
        return getJdbcTemplate().execute((ConnectionCallback<List<ProductionStep>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM production_steps where recipe_id = ? " +
                    "order by \"order\" asc");
            pstmt.setLong(1, recipeId);
            ResultSet rs = pstmt.executeQuery();
            List<ProductionStep> results = new ArrayList<>();
            int index = 0;
            while (rs.next()) {
                results.add(populateEntity(parseRs(rs), recipeId, index++));
            }
            return results;
        });
    }

    public boolean deleteByRecipe(long recipeId) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM production_steps WHERE recipe_id = ?");
            pstmt.setLong(1, recipeId);
            return pstmt.executeUpdate() != 0;
        });
    }

    public List<ProductionStep> create(List<ProductionStep> productionSteps, long recipeId) {
        if(productionSteps.isEmpty()) {
            return productionSteps;
        }
        return getJdbcTemplate().execute((ConnectionCallback<List<ProductionStep>>) con -> {
            String sqlQuery = "INSERT INTO production_steps (recipe_id, dType, message, \"order\") VALUES";
            List<String> valuesSqlString = new LinkedList<>();
            productionSteps.forEach(x -> valuesSqlString.add(" (?, ?, ?, ?)"));
            sqlQuery += String.join(",", valuesSqlString);

            PreparedStatement pstmt = con.prepareStatement(sqlQuery);
            for(int i = 0; i < productionSteps.size(); i++) {
                final int paramSize = 4;
                ProductionStep currentStep = productionSteps.get(i);
                pstmt.setLong(paramSize * i + 1, recipeId);
                pstmt.setString(paramSize * i + 2, currentStep.getClass()
                        .getAnnotation(DiscriminatorValue.class).value());
                pstmt.setInt(paramSize * i + 4, i);
                if(currentStep instanceof WrittenInstructionProductionStep) {
                    WrittenInstructionProductionStep wStep = (WrittenInstructionProductionStep) currentStep;
                    pstmt.setString(paramSize * i + 3, wStep.getMessage());
                } else {
                    pstmt.setNull(paramSize * i + 3, Types.VARCHAR);
                }
            }
            pstmt.execute();

            for(int i = 0; i < productionSteps.size(); i++) {
                ProductionStep currentStep = productionSteps.get(i);
                if(!(currentStep instanceof AddIngredientsProductionStep)) {
                    continue;
                }
                AddIngredientsProductionStep addIPs = (AddIngredientsProductionStep) currentStep;
                productionStepIngredientRepository.create(addIPs.getStepIngredients(), recipeId, i);
            }
            return productionSteps;
        });
    }

    private ProductionStep populateEntity(ProductionStep productionStep, long recipeId, int orderIndex) {
        if(productionStep instanceof AddIngredientsProductionStep) {
            AddIngredientsProductionStep addPs = (AddIngredientsProductionStep) productionStep;
            addPs.setStepIngredients(productionStepIngredientRepository
                    .loadProductionStepIngredients(recipeId, orderIndex));
        }
        return productionStep;
    }

    private ProductionStep parseRs(ResultSet rs) throws SQLException {
        String dType = rs.getString("dType");
        if(
                WrittenInstructionProductionStep.class
                .getAnnotation(DiscriminatorValue.class)
                .value().equals(dType)
        ) {
            WrittenInstructionProductionStep pStep = new WrittenInstructionProductionStep();
            pStep.setMessage(rs.getString("message"));
            return pStep;
        }

        if(
                AddIngredientsProductionStep.class
                        .getAnnotation(DiscriminatorValue.class)
                        .value().equals(dType)
        ) {
            AddIngredientsProductionStep pStep = new AddIngredientsProductionStep();
            return pStep;
        }
        throw new IllegalStateException("DAO can't handle dType: " + dType);
    }

}
