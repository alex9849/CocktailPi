package net.alex9849.cocktailpi.service.pumps.cocktailfactory;

import net.alex9849.cocktailpi.model.recipe.FeasibleRecipe;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.ManualIngredient;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CocktailFactoryTest {

    @Test
    void aggregatesAmountsPerIngredient() {
        ManualIngredient ingredientA1 = new ManualIngredient();
        ingredientA1.setId(1L);
        ingredientA1.setUnit(Ingredient.Unit.MILLILITER);

        ManualIngredient ingredientA2 = new ManualIngredient();
        ingredientA2.setId(1L);
        ingredientA2.setUnit(Ingredient.Unit.MILLILITER);

        ManualIngredient ingredientB = new ManualIngredient();
        ingredientB.setId(2L);
        ingredientB.setUnit(Ingredient.Unit.MILLILITER);

        ProductionStepIngredient stepIngredient1 = new ProductionStepIngredient();
        stepIngredient1.setIngredient(ingredientA1);
        stepIngredient1.setAmount(30);

        ProductionStepIngredient stepIngredient2 = new ProductionStepIngredient();
        stepIngredient2.setIngredient(ingredientA2);
        stepIngredient2.setAmount(20);

        ProductionStepIngredient stepIngredient3 = new ProductionStepIngredient();
        stepIngredient3.setIngredient(ingredientB);
        stepIngredient3.setAmount(10);

        AddIngredientsProductionStep step = new AddIngredientsProductionStep();
        step.setStepIngredients(List.of(stepIngredient1, stepIngredient2, stepIngredient3));

        FeasibleRecipe feasibleRecipe = new FeasibleRecipe();
        feasibleRecipe.setFeasibleProductionSteps(List.of(step));

        Map<Ingredient, Integer> result = CocktailFactory.getNeededAmountNeededPerIngredient(feasibleRecipe);

        assertEquals(2, result.size());
        assertEquals(50, result.get(ingredientA1));
        assertEquals(10, result.get(ingredientB));
    }
}
