package net.alex9849.cocktailpi;

import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.model.Collection;
import net.alex9849.cocktailpi.model.Glass;
import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.system.ErrorInfo;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.request.ExportRequest;
import net.alex9849.cocktailpi.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceIntegrationTest extends BackendIntegrationTestBase {

    @Autowired
    private AuthService authService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private EventService eventService;
    @Autowired
    private GlassService glassService;
    @Autowired
    private GpioService gpioService;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private LoadCellService loadCellService;
    @Autowired
    private PowerLimitService powerLimitService;
    @Autowired
    private PumpService pumpService;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private ReversePumpSettingsService reversePumpSettingsService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private TransferService transferService;
    @Autowired
    private UserService userService;
    @Autowired
    private WebSocketService webSocketService;

    @Test
    void authServiceGeneratesToken() {
        String token = authService.authUser("Admin", "123456", false);
        assertNotNull(token);
    }

    @Test
    void categoryServiceCreateAndDelete() {
        Category category = new Category();
        category.setName("SvcCategory");
        Category created = categoryService.createCategory(category);
        categoryService.deleteCategory(created.getId());
    }

    @Test
    void collectionServiceCreateAndDelete() {
        User owner = userService.getUsers().get(0);
        Collection collection = new Collection();
        collection.setName("SvcCollection");
        collection.setDescription("Service collection");
        collection.setOwnerId(owner.getId());
        Collection created = collectionService.createCollection(collection);
        collectionService.deleteCollection(created.getId());
    }
    

    @Test
    void eventServiceExecutionGroupsReturnsList() {
        assertNotNull(eventService.getExecutionGroups());
    }

    @Test
    void glassServiceCreateAndDelete() {
        Glass glass = new Glass();
        glass.setName("SvcGlass");
        glass.setSize(250);
        Glass created = glassService.createGlass(glass);
        glassService.deleteGlass(created.getId());
    }

    @Test
    void gpioServiceHasBoards() {
        List<GpioBoard> boards = gpioService.getGpioBoards();
        assertTrue(boards.size() > 0);
    }

    @Test
    void ingredientServiceReturnsExportList() {
        assertTrue(ingredientService.getIngredientsInExportOrder().size() > 0);
    }

    @Test
    void loadCellServiceDefaults() {
        assertNotNull(loadCellService.getDispensingAreaSettings());
    }

    @Test
    void powerLimitServiceReturnsSettings() {
        assertNotNull(powerLimitService.getPowerLimit());
    }

    @Test
    void pumpServiceReturnsList() {
        assertNotNull(pumpService.getAllPumps());
    }

    @Test
    void recipeServiceHasSeededRecipes() {
        assertTrue(recipeService.getAll().size() > 0);
    }

    @Test
    void reversePumpSettingsServiceReturnsSettings() {
        assertNotNull(reversePumpSettingsService.getReversePumpingSettings());
    }

    @Test
    void systemServiceReturnsVersionAndAppearance() {
        assertNotNull(systemService.getVersion());
        assertNotNull(systemService.getAppearance());
    }

    @Test
    void transferServiceGeneratesExport() throws Exception {
        ExportRequest request = new ExportRequest();
        request.setExportAllRecipes(true);
        request.setExportAllCollections(false);
        request.setExportCollectionIds(List.of());
        request.setExportRecipeIds(List.of());
        byte[] zip = transferService.generateExport(request);
        assertTrue(zip.length > 0);
    }

    @Test
    void userServiceReturnsUsers() {
        assertTrue(userService.getUsers().size() > 0);
    }

    @Test
    void webSocketServiceAcceptsEmptyBroadcast() {
        webSocketService.broadcastPumpLayout(List.of());
    }
}
