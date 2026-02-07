package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.model.Collection;
import net.alex9849.cocktailpi.model.Glass;
import net.alex9849.cocktailpi.model.eventaction.DoNothingEventAction;
import net.alex9849.cocktailpi.model.eventaction.EventAction;
import net.alex9849.cocktailpi.model.eventaction.EventTrigger;
import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.pump.DcPump;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.ManualIngredient;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.model.system.settings.DefaultFilterSettings;
import net.alex9849.cocktailpi.model.system.settings.ReversePumpSettings;
import net.alex9849.cocktailpi.model.user.ERole;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.gpio.LocalGpioBoardDto;
import net.alex9849.cocktailpi.payload.dto.system.settings.DefaultFilterDto;
import net.alex9849.cocktailpi.payload.dto.system.settings.PowerLimitSettingsDto;
import net.alex9849.cocktailpi.service.pumps.PumpDataService;
import net.alex9849.cocktailpi.service.pumps.PumpLockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceBehaviorTest extends BackendIntegrationTestBase {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private UserService userService;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private GlassService glassService;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private EventService eventService;
    @Autowired
    private GpioService gpioService;
    @Autowired
    private PowerLimitService powerLimitService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private ReversePumpSettingsService reversePumpSettingsService;
    @Autowired
    private PumpDataService pumpDataService;
    @Autowired
    private PumpLockService pumpLockService;

    @Test
    void categoryServiceRejectsDuplicates() {
        Category category = new Category();
        category.setName("SvcCategory");
        categoryService.createCategory(category);
        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(category));
    }

    @Test
    void collectionServiceAddRecipeAndDuplicateName() {
        User owner = userService.getUsers().get(0);
        Collection collection = new Collection();
        collection.setName("SvcCollection");
        collection.setDescription("Desc");
        collection.setOwner(owner);
        Collection created = collectionService.createCollection(collection);

        Recipe recipe = recipeService.getAll().get(0);
        assertTrue(collectionService.addRecipe(recipe.getId(), created.getId()));

        assertThrows(IllegalArgumentException.class, () -> collectionService.createCollection(collection));
    }

    @Test
    void ingredientServiceSetInBar() {
        ManualIngredient ingredient = new ManualIngredient();
        ingredient.setName("SvcIngredient");
        ingredient.setUnit(Ingredient.Unit.MILLILITER);
        ingredient.setAlcoholContent(0);
        ingredient.setInBar(false);
        Ingredient created = ingredientService.createIngredient(ingredient);

        ingredientService.setInBar(created.getId(), true);
        Ingredient reloaded = ingredientService.getIngredient(created.getId());
        assertTrue(reloaded.isInBar());
    }

    @Test
    void userServiceCreateUpdateAndDelete() {
        User user = new User();
        user.setUsername("SvcUser");
        user.setPassword("secret");
        user.setAccountNonLocked(true);
        user.setAuthority(ERole.ROLE_USER);
        User created = userService.createUser(user);

        created.setUsername("SvcUserUpdated");
        userService.updateUser(created, false);

        userService.deleteUser(created.getId());
    }

    @Test
    void recipeServiceCreateAndFilter() {
        ManualIngredient ingredient = new ManualIngredient();
        ingredient.setName("SvcRecipeIngredient");
        ingredient.setUnit(Ingredient.Unit.MILLILITER);
        ingredient.setAlcoholContent(0);
        ingredient.setInBar(false);
        Ingredient createdIngredient = ingredientService.createIngredient(ingredient);

        ProductionStepIngredient psi = new ProductionStepIngredient();
        psi.setIngredient(createdIngredient);
        psi.setAmount(10);
        psi.setScale(false);
        psi.setBoostable(false);
        AddIngredientsProductionStep step = new AddIngredientsProductionStep();
        step.setStepIngredients(List.of(psi));

        User owner = userService.getUsers().get(0);
        Recipe recipe = new Recipe();
        recipe.setName("SvcRecipe");
        recipe.setOwner(owner);
        recipe.setCategories(List.of(categoryService.getAllCategories().get(0)));
        recipe.setProductionSteps(List.of(step));

        Recipe created = recipeService.createRecipe(recipe);
        assertNotNull(recipeService.getById(created.getId()));
    }

    @Test
    void glassServiceCreateAndFindByName() {
        Glass glass = new Glass();
        glass.setName("SvcGlass");
        glass.setSize(250);
        glassService.createGlass(glass);
        assertNotNull(glassService.getByName("SvcGlass"));
    }

    @Test
    void eventServiceCreateAndDelete() {
        DoNothingEventAction action = new DoNothingEventAction();
        action.setTrigger(EventTrigger.COCKTAIL_PRODUCTION_STARTED);
        action.setComment("Svc event");
        action.setExecutionGroups(Set.of());
        EventAction created = eventService.createEventAction(action);
        eventService.deleteEventAction(created.getId());
    }

    @Test
    void gpioServiceFromDtoAndStatus() {
        LocalGpioBoardDto.Request.Create dto = new LocalGpioBoardDto.Request.Create();
        dto.setName("SvcLocal");
        GpioBoard board = gpioService.fromDto(dto);
        assertEquals("SvcLocal", board.getName());
        assertNotNull(gpioService.getGpioStatus());
    }

    @Test
    void powerLimitServiceSetAndGet() {
        PowerLimitSettingsDto.Duplex.Detailed settings = new PowerLimitSettingsDto.Duplex.Detailed();
        settings.setEnable(true);
        settings.setLimit(99999);
        PowerLimitSettingsDto.Duplex.Detailed saved = powerLimitService.setPowerLimit(settings);
        assertTrue(saved.isEnable());

        PowerLimitSettingsDto.Duplex.Detailed disabled = new PowerLimitSettingsDto.Duplex.Detailed();
        disabled.setEnable(false);
        powerLimitService.setPowerLimit(disabled);
    }

    @Test
    void systemServiceDefaultFilterAndDonation() {
        DefaultFilterSettings settings = new DefaultFilterSettings();
        settings.setEnable(true);
        DefaultFilterSettings.Filter filter = new DefaultFilterSettings.Filter();
        filter.setFabricable(RecipeService.FabricableFilter.AUTOMATICALLY);
        settings.setFilter(filter);
        DefaultFilterSettings saved = systemService.setDefaultFilterSettings(settings);
        assertTrue(saved.isEnable());

        DefaultFilterDto.Duplex.Detailed dto = new DefaultFilterDto.Duplex.Detailed();
        DefaultFilterDto.Duplex.Detailed.Filter dtoFilter = new DefaultFilterDto.Duplex.Detailed.Filter();
        dtoFilter.setFabricable("auto");
        dto.setEnable(true);
        dto.setFilter(dtoFilter);
        DefaultFilterSettings fromDto = systemService.fromDto(dto);
        assertEquals(RecipeService.FabricableFilter.AUTOMATICALLY, fromDto.getFilter().getFabricable());

        systemService.setDonated(true);
        assertNotNull(systemService.getGlobalSettings());
    }

    @Test
    void reversePumpSettingsServiceRoundTrip() {
        ReversePumpSettings settings = reversePumpSettingsService.getReversePumpingSettings();
        assertNotNull(settings);
    }

    @Test
    void pumpDataServiceCreateAndUpdate() {
        GpioBoard board = gpioService.getGpioBoards().get(0);
        DcPump pump = new DcPump();
        pump.setName("SvcPump");
        pump.setPin(board.getPin(board.getMinPin()));
        pump.setTimePerClInMs(1000);
        pump.setIsPowerStateHigh(false);
        pump.setPumpedUp(false);
        pump.setFillingLevelInMl(100);
        pump.setTubeCapacityInMl(5.0);
        pump.setPowerConsumption(100);
        Pump created = pumpDataService.createPump(pump);

        created.setName("SvcPumpUpdated");
        pumpDataService.updatePump(created);
    }

    @Test
    void pumpLockServiceBasicBehavior() {
        Object owner = new Object();
        assertTrue(pumpLockService.testAndAcquirePumpLock(1L, owner));
        pumpLockService.releasePumpLock(1L, owner);
        assertTrue(pumpLockService.testAndAcquireGlobal(owner));
        pumpLockService.releaseGlobal(owner);
    }
}
