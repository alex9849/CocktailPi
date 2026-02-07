package net.alex9849.cocktailpi.repository;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.ManualIngredient;
import net.alex9849.cocktailpi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductionStepIngredientRepositoryTest extends BackendIntegrationTestBase {

    @Autowired
    private ProductionStepIngredientRepository productionStepIngredientRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createAndLoadStepIngredients() {
        ManualIngredient ingredient = new ManualIngredient();
        ingredient.setName("RepoPsiIngredient");
        ingredient.setUnit(Ingredient.Unit.MILLILITER);
        ingredient.setAlcoholContent(0);
        ingredient.setInBar(false);
        Ingredient createdIngredient = ingredientRepository.create(ingredient);

        ProductionStepIngredient psi = new ProductionStepIngredient();
        psi.setIngredient(createdIngredient);
        psi.setAmount(15);
        psi.setScale(true);
        psi.setBoostable(false);

        User owner = userRepository.findAll().get(0);
        Recipe recipe = new Recipe();
        recipe.setName("RepoPsiRecipe");
        recipe.setOwner(owner);
        AddIngredientsProductionStep step = new AddIngredientsProductionStep();
        step.setStepIngredients(List.of(psi));
        recipe.setProductionSteps(List.of(step));
        Recipe createdRecipe = recipeRepository.create(recipe);

        List<ProductionStepIngredient> loaded = productionStepIngredientRepository
                .loadProductionStepIngredients(createdRecipe.getId(), 0);
        assertEquals(1, loaded.size());
        assertEquals(15, loaded.get(0).getAmount());
    }
}
