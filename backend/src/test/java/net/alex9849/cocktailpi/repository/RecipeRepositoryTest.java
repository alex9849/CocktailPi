package net.alex9849.cocktailpi.repository;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.ManualIngredient;
import net.alex9849.cocktailpi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeRepositoryTest extends BackendIntegrationTestBase {

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createUpdateFindAndDeleteRecipe() {
        ManualIngredient ingredient = new ManualIngredient();
        ingredient.setName("RepoRecipeIng");
        ingredient.setUnit(Ingredient.Unit.MILLILITER);
        ingredient.setAlcoholContent(0);
        ingredient.setInBar(false);
        Ingredient createdIngredient = ingredientRepository.create(ingredient);

        ProductionStepIngredient psi = new ProductionStepIngredient();
        psi.setIngredient(createdIngredient);
        psi.setAmount(10);
        psi.setScale(false);
        psi.setBoostable(false);
        AddIngredientsProductionStep step = new AddIngredientsProductionStep();
        step.setStepIngredients(List.of(psi));

        User owner = userRepository.findAll().get(0);
        Category category = categoryRepository.findAll().get(0);
        Recipe recipe = new Recipe();
        recipe.setName("RepoRecipe");
        recipe.setDescription("Repo recipe description");
        recipe.setOwner(owner);
        recipe.setCategories(List.of(category));
        recipe.setProductionSteps(List.of(step));

        Recipe created = recipeRepository.create(recipe);
        assertNotNull(created);

        Optional<Recipe> found = recipeRepository.findById(created.getId());
        assertTrue(found.isPresent());

        created.setDescription("Updated");
        assertTrue(recipeRepository.update(created));

        byte[] image = "img".getBytes(StandardCharsets.UTF_8);
        recipeRepository.setImage(created.getId(), image);
        assertTrue(recipeRepository.getImage(created.getId()).isPresent());

        assertTrue(recipeRepository.delete(created.getId()));
    }

    @Test
    void queryHelpersReturnMatches() {
        Recipe recipe = recipeRepository.findAll().get(0);
        Set<Long> byName = recipeRepository.getIdsByName(recipe.getName());
        assertTrue(byName.contains(recipe.getId()));

        Set<Long> byOwner = recipeRepository.getIdsByOwnerId(recipe.getOwnerId());
        assertTrue(byOwner.contains(recipe.getId()));

        Set<Long> contains = recipeRepository.getIdsContainingName(recipe.getName().substring(0, 2));
        assertTrue(contains.contains(recipe.getId()));

        List<Recipe> page = recipeRepository.findByIds(0, 5, Sort.by(Sort.Direction.ASC, "name"), recipe.getId());
        assertEquals(1, page.size());
    }
}
