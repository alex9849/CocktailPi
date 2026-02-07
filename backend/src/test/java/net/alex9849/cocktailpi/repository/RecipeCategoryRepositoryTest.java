package net.alex9849.cocktailpi.repository;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeCategoryRepositoryTest extends BackendIntegrationTestBase {

    @Autowired
    private RecipeCategoryRepository recipeCategoryRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void addAndRemoveCategoryMapping() {
        Recipe recipe = recipeRepository.findAll().get(0);
        Category category = categoryRepository.findAll().get(0);

        recipeCategoryRepository.addToCategory(recipe.getId(), category.getId());
        Set<Long> recipeIds = recipeRepository.getIdsInCategory(category.getId());
        assertTrue(recipeIds.contains(recipe.getId()));

        recipeCategoryRepository.removeFromAllCategories(recipe.getId());
    }
}
