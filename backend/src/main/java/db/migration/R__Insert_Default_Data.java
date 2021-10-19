package db.migration;

import net.alex9849.cocktailmaker.model.Category;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ProductionStepIngredient;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.service.CategoryService;
import net.alex9849.cocktailmaker.service.IngredientService;
import net.alex9849.cocktailmaker.service.RecipeService;
import net.alex9849.cocktailmaker.service.UserService;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.util.Map;
import java.util.stream.Collectors;

public class R__Insert_Default_Data extends BaseJavaMigration {

    private final UserService userService = SpringUtility.getBean(UserService.class);
    private final RecipeService recipeService = SpringUtility.getBean(RecipeService.class);
    private final CategoryService categoryService = SpringUtility.getBean(CategoryService.class);
    private final IngredientService ingredientService = SpringUtility.getBean(IngredientService.class);

    private Map<String, Category> categoriesByName = null;
    private Map<String, Ingredient> ingredientsByName = null;

    @Override
    public void migrate(Context context) throws Exception {
        if(!userService.getUsers().isEmpty()) {
            return;
        }
        User defaultUser = createDefaultUser();
    }

    private User createDefaultUser() {
        User defaultUser = new User();
        defaultUser.setUsername("Admin");
        defaultUser.setFirstname("Admin");
        defaultUser.setLastname("Example");
        defaultUser.setEmail("admin@localhost.local");
        defaultUser.setPassword("123456");
        defaultUser.setAccountNonLocked(true);
        defaultUser.setAuthority(ERole.ROLE_ADMIN);
        return userService.createUser(defaultUser);
    }

    private Recipe createRecipe(Recipe recipe) {
        for(ProductionStepIngredient recipeIngredient : recipe.getProductionSteps()) {
            Ingredient ingredient = getOrCreateIngredient(recipeIngredient.getIngredient());
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setIngredientId(ingredient.getId());
        }
        for(int i = 0; i < recipe.getCategories().size(); i++) {
            recipe.getCategories().set(i, getOrCreateCategory(recipe.getCategories().get(i)));
        }
        return recipeService.createRecipe(recipe);
    }

    private Ingredient getOrCreateIngredient(Ingredient searchIngredient) {
        if(ingredientsByName == null) {
            ingredientsByName = ingredientService.getIngredientByFilter(null, false)
                    .stream().collect(Collectors.toMap(Ingredient::getName, x -> x));
        }
        if(!ingredientsByName.containsKey(searchIngredient.getName())) {
            Ingredient createdIngredient = ingredientService.createIngredient(searchIngredient);
            ingredientsByName.put(createdIngredient.getName(), createdIngredient);
        }
        return ingredientsByName.get(searchIngredient.getName());
    }

    private Category getOrCreateCategory(Category searchCategory) {
        if(categoriesByName == null) {
            categoriesByName = categoryService.getAllCategories().stream()
                    .collect(Collectors.toMap(Category::getName, x -> x));
        }
        if(!categoriesByName.containsKey(searchCategory.getName())) {
            Category createdCategory = categoryService.createCategory(searchCategory);
            categoriesByName.put(createdCategory.getName(), createdCategory);
        }
        return categoriesByName.get(searchCategory.getName());
    }


}
