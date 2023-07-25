package net.alex9849.cocktailmaker.config.seed;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import net.alex9849.cocktailmaker.model.Category;
import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.model.pump.StepperPump;
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
import net.alex9849.cocktailmaker.service.pumps.PumpDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SeedDataInserter {
    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserService userService;

    @Autowired
    private PumpDataService pumpService;

    Logger logger = LoggerFactory.getLogger(SeedDataInserter.class);

    @Value("${alex9849.app.demoMode}")
    private boolean isDemoMode;

    @Value("${alex9849.app.devMode}")
    private boolean isDevMode;

    public void migrate() throws Exception {
        if(!userService.getUsers().isEmpty()) {
            return;
        }
        logger.info("Inserting seed data into database...");

        User defaultUser = createDefaultUser();
        Map<Long, Long> ingredientsOldIdToNewIdMap = this.migrateIngredients();
        Map<Long, Long> categoriesOldIdToNewIdMap = this.migrateCategories();
        this.migrateRecipes(defaultUser, ingredientsOldIdToNewIdMap, categoriesOldIdToNewIdMap);
        if(isDemoMode || isDevMode) {
            this.createDemoPumps();
        }
        logger.info("Finished inserting seed data into database.");
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
        Map<Long, Long> childParentRelation = new HashMap<>();
        List<IngredientDto.Response.Detailed> ingredientDtos = loadFromFile("/db/defaultdata/ingredients.json", IngredientDto.Response.Detailed.class);

        for(IngredientDto.Response.Detailed ingredientDto : ingredientDtos) {
            IngredientDto.Request.Create createDto = IngredientDto.Request.Create.fromDetailedDto(ingredientDto);
            if(createDto.getParentGroupId() != null) {
                Long parentGroupId = createDto.getParentGroupId();
                childParentRelation.put(ingredientDto.getId(), parentGroupId);
                createDto.setParentGroupId(null);
            }
            Ingredient createdIngredient = ingredientService.createIngredient(ingredientService.fromDto(createDto));
            oldToNewIdMap.put(ingredientDto.getId(), createdIngredient.getId());
        }
        for(Map.Entry<Long, Long> entry : childParentRelation.entrySet()) {
            Long child = oldToNewIdMap.get(entry.getKey());
            Long parent = oldToNewIdMap.get(entry.getValue());
            Ingredient ingredient = ingredientService.getIngredient(child);
            ingredient.setParentGroupId(parent);
            ingredientService.updateIngredient(ingredient);
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

    private void createDemoPumps() {
        DcPump dcPump = new DcPump();
        dcPump.setPumpedUp(false);
        dcPump.setFillingLevelInMl(3000);
        dcPump.setIsPowerStateHigh(false);
        dcPump.setTimePerClInMs(3000);
        dcPump.setTubeCapacityInMl(5.0);

        final int nrPumps = 8;
        for(int i = 0; i < nrPumps / 2; i++) {
            dcPump.setPin(i);
            this.pumpService.createPump(dcPump);
        }

        StepperPump stepperPump = new StepperPump();
        stepperPump.setPumpedUp(false);
        stepperPump.setFillingLevelInMl(3000);
        stepperPump.setStepsPerCl(20);
        stepperPump.setAcceleration(10);
        stepperPump.setMaxStepsPerSecond(20);
        stepperPump.setTubeCapacityInMl(5.0);
        for(int i = nrPumps / 2; i < nrPumps; i++) {
            stepperPump.setEnablePin(i + (i * 2));
            stepperPump.setStepPin(i + (i * 2) + 1);
            this.pumpService.createPump(stepperPump);
        }
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
