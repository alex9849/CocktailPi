package db.migration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import net.alex9849.cocktailmaker.model.Category;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.category.CategoryDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.IngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.AddIngredientsProductionStepDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.ProductionStepDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.ProductionStepIngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.WrittenInstructionProductionStepDto;
import net.alex9849.cocktailmaker.service.CategoryService;
import net.alex9849.cocktailmaker.service.IngredientService;
import net.alex9849.cocktailmaker.service.RecipeService;
import net.alex9849.cocktailmaker.service.UserService;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class R__Insert_Default_Data extends BaseJavaMigration {
    private final IngredientService ingredientService = SpringUtility.getBean(IngredientService.class);
    private final CategoryService categoryService = SpringUtility.getBean(CategoryService.class);
    private final RecipeService recipeService = SpringUtility.getBean(RecipeService.class);
    private final UserService userService = SpringUtility.getBean(UserService.class);

    @Override
    public void migrate(Context context) throws Exception {
        if(!userService.getUsers().isEmpty()) {
            return;
        }

        User defaultUser = createDefaultUser();
        Map<Long, Long> ingredientsOldIdToNewIdMap = this.migrateIngredients();
        Map<Long, Long> categoriesOldIdToNewIdMap = this.migrateCategories();
        this.migrateRecipes(defaultUser, ingredientsOldIdToNewIdMap, categoriesOldIdToNewIdMap);
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

    private Map<Long, Long> migrateIngredients() throws IOException {
        Map<Long, Long> oldToNewIdMap = new HashMap<>();
        List<IngredientDto.Response.Detailed> ingredientDtos = loadFromFile("/db/defaultdata/ingredients.json", IngredientDto.Response.Detailed.class);

        for(IngredientDto.Response.Detailed ingredientDto : ingredientDtos) {
            IngredientDto.Request.Create createDto = IngredientDto.Request.Create.fromDetailedDto(ingredientDto);
            if(createDto.getParentGroupId() != null) {
                createDto.setParentGroupId(oldToNewIdMap.get(createDto.getParentGroupId()));
            }
            Ingredient createdIngredient = ingredientService.createIngredient(ingredientService.fromDto(createDto));
            oldToNewIdMap.put(ingredientDto.getId(), createdIngredient.getId());
        }
        return oldToNewIdMap;
    }

    private Map<Long, Long> migrateCategories() throws IOException {
        Map<Long, Long> oldToNewIdMap = new HashMap<>();
        List<CategoryDto.Duplex.Detailed> categoryDtos = loadFromFile("/db/defaultdata/categories.json", CategoryDto.Duplex.Detailed.class);

        for(CategoryDto.Duplex.Detailed categoryDto : categoryDtos) {
            CategoryDto.Request.Create createDto = new CategoryDto.Request.Create(categoryDto);
            Category createdCategory = categoryService.createCategory(categoryService.fromDto(createDto));
            oldToNewIdMap.put(categoryDto.getId(), createdCategory.getId());
        }
        return oldToNewIdMap;
    }

    private void migrateRecipes(User owner, Map<Long, Long> ingredientsOldIdToNewIdMap, Map<Long, Long> categoriesOldIdToNewIdMap) throws IOException {
        List<RecipeDto.Response.Detailed> recipeDtos = loadFromFile("/db/defaultdata/recipes.json", RecipeDto.Response.Detailed.class);

        for(RecipeDto.Response.Detailed recipeDto : recipeDtos) {
            RecipeDto.Request.Create createDto = new RecipeDto.Request.Create(recipeDto);
            createDto.setOwnerId(owner.getId());
            createDto.setCategoryIds(createDto.getCategoryIds()
                    .stream().map(categoriesOldIdToNewIdMap::get)
                    .collect(Collectors.toSet()));

            //Change IngredientIds
            for(ProductionStepDto.Request.Create pStep : createDto.getProductionSteps()) {
                if(pStep instanceof WrittenInstructionProductionStepDto.Request.Create) {
                    continue;
                }
                if(!(pStep instanceof AddIngredientsProductionStepDto.Request.Create)) {
                    throw new IllegalStateException("Unknown class type: " + pStep.getClass().getName());
                }
                AddIngredientsProductionStepDto.Request.Create aiPStep = (AddIngredientsProductionStepDto.Request.Create) pStep;
                for(ProductionStepIngredientDto.Request.Create ingredient : aiPStep.getStepIngredients()) {
                    ingredient.setIngredientId(ingredientsOldIdToNewIdMap.get(ingredient.getIngredientId()));
                }
            }
            Recipe recipe = recipeService.createRecipe(recipeService.fromDto(createDto));

            InputStream recipeImageStream = this.getClass().getResourceAsStream("/db/defaultdata/images/" + recipeDto.getName() + ".jpg");
            if(recipeImageStream != null) {
                BufferedImage image = ImageIO.read(recipeImageStream);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", out);
                recipeService.setImage(recipe.getId(), out.toByteArray());
            }


        }
    }

    private <T> List<T> loadFromFile(String path, Class<T> typeClass) throws IOException {
        InputStream recipeStream = this.getClass().getResourceAsStream(path);
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, typeClass);
        return mapper.readValue(recipeStream, collectionType);
    }
}
