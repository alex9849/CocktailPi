package net.alex9849.cocktailpi.repository;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.model.recipe.productionstep.WrittenInstructionProductionStep;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.ManualIngredient;
import net.alex9849.cocktailpi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductionStepRepositoryTest extends BackendIntegrationTestBase {

    @Autowired
    private ProductionStepRepository productionStepRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void createAndLoadProductionSteps() {
        ManualIngredient ingredient = new ManualIngredient();
        ingredient.setName("RepoStepIng");
        ingredient.setUnit(Ingredient.Unit.MILLILITER);
        ingredient.setAlcoholContent(0);
        ingredient.setInBar(false);
        Ingredient createdIngredient = ingredientRepository.create(ingredient);

        ProductionStepIngredient psi = new ProductionStepIngredient();
        psi.setIngredient(createdIngredient);
        psi.setAmount(20);
        psi.setScale(false);
        psi.setBoostable(false);

        AddIngredientsProductionStep addStep = new AddIngredientsProductionStep();
        addStep.setStepIngredients(List.of(psi));

        WrittenInstructionProductionStep writtenStep = new WrittenInstructionProductionStep();
        writtenStep.setMessage("Stir");

        User owner = userRepository.findAll().get(0);
        Category category = categoryRepository.findAll().get(0);

        Recipe recipe = new Recipe();
        recipe.setName("RepoStepRecipe");
        recipe.setOwner(owner);
        recipe.setCategories(List.of(category));
        recipe.setProductionSteps(List.of(addStep, writtenStep));

        Recipe createdRecipe = recipeRepository.create(recipe);
        List<ProductionStep> steps = productionStepRepository.loadByRecipeId(createdRecipe.getId());
        assertEquals(2, steps.size());
        assertTrue(steps.get(0) instanceof AddIngredientsProductionStep);
    }
}
