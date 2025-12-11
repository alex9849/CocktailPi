package net.alex9849.cocktailpi.config.seed;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.model.Glass;
import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.gpio.local.LocalGpioBoard;
import net.alex9849.cocktailpi.model.pump.DcPump;
import net.alex9849.cocktailpi.model.pump.StepperPump;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.user.ERole;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.category.CategoryDto;
import net.alex9849.cocktailpi.payload.dto.glass.GlassDto;
import net.alex9849.cocktailpi.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.IngredientDto;
import net.alex9849.cocktailpi.payload.dto.recipe.productionstep.AddIngredientsProductionStepDto;
import net.alex9849.cocktailpi.payload.dto.recipe.productionstep.ProductionStepDto;
import net.alex9849.cocktailpi.payload.dto.recipe.productionstep.ProductionStepIngredientDto;
import net.alex9849.cocktailpi.payload.dto.recipe.productionstep.WrittenInstructionProductionStepDto;
import net.alex9849.cocktailpi.service.*;
import net.alex9849.cocktailpi.service.pumps.PumpDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
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

    @Autowired
    private GpioService gpioService;

    @Autowired
    private GlassService glassService;

    Logger logger = LoggerFactory.getLogger(SeedDataInserter.class);

    @Value("${alex9849.app.demoMode}")
    private boolean isDemoMode;

    @Value("${alex9849.app.devMode}")
    private boolean isDevMode;

    public void migrate() throws Exception {
        if (!userService.getUsers().isEmpty()) {
            return;
        }
        logger.info("Inserting seed data into database...");

        User defaultUser = createDefaultUser();
        LocalGpioBoard gpioBoard = createLocalGpioBoard();
        Map<Long, Long> ingredientsOldIdToNewIdMap = this.migrateIngredients();
        Map<Long, Long> categoriesOldIdToNewIdMap = this.migrateCategories();
        Map<Long, Long> glassesOldIdToNewIdMap = this.migrateGlasses();
        this.migrateRecipes(defaultUser, ingredientsOldIdToNewIdMap, categoriesOldIdToNewIdMap, glassesOldIdToNewIdMap);
        if (isDemoMode || isDevMode) {
            this.createDemoPumps(gpioBoard);
        }
        logger.info("Finished inserting seed data into database.");
    }

    private Map<Long, Long> migrateGlasses() throws IOException {
        Map<Long, Long> oldToNewIdMap = new HashMap<>();
        List<GlassDto.Duplex.Detailed> glassDtos = loadFromFile("/db/defaultdata/glasses.json", GlassDto.Duplex.Detailed.class);

        for (GlassDto.Duplex.Detailed glassDto : glassDtos) {
            Glass createdGlass = glassService.createGlass(glassService.fromDto(glassDto));
            oldToNewIdMap.put(glassDto.getId(), createdGlass.getId());
        }
        return oldToNewIdMap;
    }

    private User createDefaultUser() {
        User defaultUser = new User();
        defaultUser.setUsername("Admin");
        defaultUser.setPassword("123456");
        defaultUser.setAccountNonLocked(true);
        defaultUser.setAuthority(ERole.ROLE_SUPER_ADMIN);
        return userService.createUser(defaultUser);
    }

    public LocalGpioBoard createLocalGpioBoard() {
        LocalGpioBoard board = new LocalGpioBoard();
        board.setName("Local board");
        return (LocalGpioBoard) gpioService.createGpioBoard(board);
    }

    private Map<Long, Long> migrateIngredients() throws IOException {
        Map<Long, Long> oldToNewIdMap = new HashMap<>();
        Map<Long, Long> childParentRelation = new HashMap<>();
        List<IngredientDto.Response.Detailed> ingredientDtos = loadFromFile("/db/defaultdata/ingredients.json", IngredientDto.Response.Detailed.class);

        for (IngredientDto.Response.Detailed ingredientDto : ingredientDtos) {
            IngredientDto.Request.Create createDto = IngredientDto.Request.Create.fromDetailedDto(ingredientDto);
            if (createDto.getParentGroupId() != null) {
                Long parentGroupId = createDto.getParentGroupId();
                childParentRelation.put(ingredientDto.getId(), parentGroupId);
                createDto.setParentGroupId(null);
            }
            Ingredient createdIngredient = ingredientService.createIngredient(ingredientService.fromDto(createDto));
            oldToNewIdMap.put(ingredientDto.getId(), createdIngredient.getId());

            InputStream ingredientImageStream = this.getClass().getResourceAsStream("/db/defaultdata/images/ingredients/" + createdIngredient.getName() + ".jpg");
            if (ingredientImageStream != null) {
                BufferedImage image = ImageIO.read(ingredientImageStream);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", out);
                ingredientService.setImage(createdIngredient.getId(), out.toByteArray());
            }

        }
        for (Map.Entry<Long, Long> entry : childParentRelation.entrySet()) {
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

        for (CategoryDto.Duplex.Detailed categoryDto : categoryDtos) {
            CategoryDto.Request.Create createDto = new CategoryDto.Request.Create(categoryDto);
            Category createdCategory = categoryService.createCategory(categoryService.fromDto(createDto));
            oldToNewIdMap.put(categoryDto.getId(), createdCategory.getId());
        }
        return oldToNewIdMap;
    }

    private void createDemoPumps(GpioBoard gpioBoard) {
        DcPump dcPump = new DcPump();
        dcPump.setPumpedUp(false);
        dcPump.setFillingLevelInMl(3000);
        dcPump.setIsPowerStateHigh(false);
        dcPump.setTimePerClInMs(3000);
        dcPump.setTubeCapacityInMl(5.0);

        final int nrPumps = 8;
        final int nrDcPumps = 4;
        for (int i = 0; i < nrDcPumps; i++) {
            dcPump.setPin(gpioBoard.getPin(i));
            this.pumpService.createPump(dcPump);
        }

        StepperPump stepperPump = new StepperPump();
        stepperPump.setPumpedUp(false);
        stepperPump.setFillingLevelInMl(3000);
        stepperPump.setStepsPerCl(20);
        stepperPump.setAcceleration(10);
        stepperPump.setMaxStepsPerSecond(20);
        stepperPump.setTubeCapacityInMl(5.0);
        for (int i = nrDcPumps; i < nrPumps; i++) {
            stepperPump.setEnablePin(gpioBoard.getPin(nrDcPumps + ((i - nrDcPumps) * 2)));
            stepperPump.setStepPin(gpioBoard.getPin(nrDcPumps + ((i - nrDcPumps) * 2) + 1));
            this.pumpService.createPump(stepperPump);
        }
    }

    private void migrateRecipes(User owner, Map<Long, Long> ingredientsOldIdToNewIdMap,
                                Map<Long, Long> categoriesOldIdToNewIdMap, Map<Long, Long> glassesOldIdToNewIdMap) throws IOException {
        List<RecipeDto.Response.Detailed> recipeDtos = loadFromFile("/db/defaultdata/recipes.json", RecipeDto.Response.Detailed.class);

        for (RecipeDto.Response.Detailed recipeDto : recipeDtos) {
            RecipeDto.Request.Create createDto = new RecipeDto.Request.Create(recipeDto);
            createDto.setOwnerId(owner.getId());
            createDto.setCategoryIds(createDto.getCategoryIds()
                    .stream().map(categoriesOldIdToNewIdMap::get)
                    .collect(Collectors.toSet()));
            if(createDto.getDefaultGlassId() != null) {
                createDto.setDefaultGlassId(glassesOldIdToNewIdMap.get(createDto.getDefaultGlassId()));
            }

            //Change IngredientIds
            for (ProductionStepDto.Request.Create pStep : createDto.getProductionSteps()) {
                if (pStep instanceof WrittenInstructionProductionStepDto.Request.Create) {
                    continue;
                }
                if (!(pStep instanceof AddIngredientsProductionStepDto.Request.Create)) {
                    throw new IllegalStateException("Unknown class type: " + pStep.getClass().getName());
                }
                AddIngredientsProductionStepDto.Request.Create aiPStep = (AddIngredientsProductionStepDto.Request.Create) pStep;
                for (ProductionStepIngredientDto.Request.Create ingredient : aiPStep.getStepIngredients()) {
                    ingredient.setIngredientId(ingredientsOldIdToNewIdMap.get(ingredient.getIngredientId()));
                }
            }
            Recipe recipe = recipeService.createRecipe(recipeService.fromDto(createDto));

            InputStream recipeImageStream = this.getClass().getResourceAsStream("/db/defaultdata/images/recipes/" + recipeDto.getNormalName() + ".jpg");
            if (recipeImageStream != null) {
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
