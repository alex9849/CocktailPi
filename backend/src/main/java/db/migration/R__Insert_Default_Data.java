package db.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.IngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.AddIngredientsProductionStepDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.ProductionStepDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.ProductionStepIngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.WrittenInstructionProductionStepDto;
import net.alex9849.cocktailmaker.service.IngredientService;
import net.alex9849.cocktailmaker.service.RecipeService;
import net.alex9849.cocktailmaker.service.UserService;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class R__Insert_Default_Data extends BaseJavaMigration {
    private final IngredientService ingredientService = SpringUtility.getBean(IngredientService.class);
    private final RecipeService recipeService = SpringUtility.getBean(RecipeService.class);
    private final UserService userService = SpringUtility.getBean(UserService.class);

    @Override
    public void migrate(Context context) throws Exception {
        if(!userService.getUsers().isEmpty()) {
            return;
        }

        User defaultUser = createDefaultUser();
        Map<Long, Long> ingredientsOldIdToNewIdMap = this.migrateIngredients();
        this.migrateRecipes(defaultUser, ingredientsOldIdToNewIdMap);
    }

    private User createDefaultUser() {
        User defaultUser = new User();
        defaultUser.setUsername("Admin");
        defaultUser.setFirstname("Admin");
        defaultUser.setLastname("Example");
        defaultUser.setEmail("admin@localhost.local");

        defaultUser.setPassword("$2a$10$foQL7xSRCK53J/G.MIauWOnllOS9.vyIT5RtUQT25t5ref07MtCfe");
        defaultUser.setAccountNonLocked(true);
        defaultUser.setAuthority(ERole.ROLE_ADMIN);
        return userService.createUser(defaultUser);
    }

    /**
     *
     * @return Map that mapps the old ingredientId to the new one
     */
    private Map<Long, Long> migrateIngredients() throws IOException {
        Map<Long, Long> oldToNewIdMap = new HashMap<>();
        List<IngredientDto.Response.Detailed> ingredientDtos = loadFromFile("/db/defaultdata/ingredients.json");

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

    private void migrateRecipes(User owner, Map<Long, Long> ingredientsOldIdToNewIdMap) throws IOException {
        List<RecipeDto.Response.Detailed> recipeDtos = loadFromFile("/db/defaultdata/recipes.json");

        for(RecipeDto.Response.Detailed recipeDto : recipeDtos) {
            RecipeDto.Request.Create createDto = new RecipeDto.Request.Create(recipeDto);
            createDto.setOwnerId(owner.getId());

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
        }
    }

    private <T> List<T> loadFromFile(String path) throws IOException {
        InputStream recipeStream = this.getClass().getResourceAsStream(path);
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeReference<List<T>> typeReference = new TypeReference<>(){};
        return mapper.readValue(recipeStream, typeReference);
    }
}
