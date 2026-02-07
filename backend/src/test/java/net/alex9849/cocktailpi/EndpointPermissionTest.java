package net.alex9849.cocktailpi;

import com.fasterxml.jackson.databind.JsonNode;
import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.model.Glass;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.user.ERole;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.pump.PumpDto;
import net.alex9849.cocktailpi.service.CategoryService;
import net.alex9849.cocktailpi.service.GlassService;
import net.alex9849.cocktailpi.service.IngredientService;
import net.alex9849.cocktailpi.service.PumpService;
import net.alex9849.cocktailpi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EndpointPermissionTest extends BackendIntegrationTestBase {

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private GlassService glassService;
    @Autowired
    private PumpService pumpService;

    private static final class AuthUser {
        private final User user;
        private final String token;

        private AuthUser(User user, String token) {
            this.user = user;
            this.token = token;
        }
    }

    private AuthUser createUserToken(ERole role) throws Exception {
        User user = new User();
        user.setUsername("test_" + role.name().toLowerCase() + "_" + UUID.randomUUID());
        user.setPassword("password");
        user.setAccountNonLocked(true);
        user.setAuthority(role);
        userService.createUser(user);
        String token = loginAndGetToken(user.getUsername(), "password");
        return new AuthUser(user, token);
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String payload = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"remember\":false}";
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("accessToken").asText();
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    @Test
    void userCannotCreateCategoryButCanRead() throws Exception {
        AuthUser user = createUserToken(ERole.ROLE_USER);
        mockMvc.perform(get("/api/category/")
                        .header("Authorization", bearer(user.token)))
                .andExpect(status().isOk());

        String payload = "{\"name\":\"PermCategory_" + UUID.randomUUID() + "\"}";
        mockMvc.perform(post("/api/category/")
                        .header("Authorization", bearer(user.token))
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateCategory() throws Exception {
        AuthUser admin = createUserToken(ERole.ROLE_ADMIN);
        String payload = "{\"name\":\"PermCategoryAdmin_" + UUID.randomUUID() + "\"}";
        mockMvc.perform(post("/api/category/")
                        .header("Authorization", bearer(admin.token))
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());
    }

    @Test
    void recipeCreatorCanCreateRecipe() throws Exception {
        AuthUser creator = createUserToken(ERole.ROLE_RECIPE_CREATOR);

        List<Category> categories = categoryService.getAllCategories();
        List<Ingredient> ingredients = ingredientService.getIngredientsInExportOrder();
        Glass glass = glassService.getAll().get(0);
        assertTrue(!categories.isEmpty() && !ingredients.isEmpty());

        String recipeJson = "{"
                + "\"name\":\"PermRecipe_" + UUID.randomUUID() + "\","
                + "\"ownerId\":" + creator.user.getId() + ","
                + "\"description\":\"test\","
                + "\"productionSteps\":[{"
                + "\"type\":\"addIngredients\","
                + "\"stepIngredients\":[{"
                + "\"ingredientId\":" + ingredients.get(0).getId() + ","
                + "\"amount\":10,"
                + "\"scale\":false,"
                + "\"boostable\":false"
                + "}]"
                + "}],"
                + "\"categoryIds\":[" + categories.get(0).getId() + "],"
                + "\"defaultGlassId\":" + glass.getId()
                + "}";

        MockMultipartFile recipePart = new MockMultipartFile(
                "recipe",
                "recipe.json",
                MediaType.APPLICATION_JSON_VALUE,
                recipeJson.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/recipe/")
                        .file(recipePart)
                        .header("Authorization", bearer(creator.token))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }

    @Test
    void userCannotCreateRecipe() throws Exception {
        AuthUser user = createUserToken(ERole.ROLE_USER);

        List<Category> categories = categoryService.getAllCategories();
        List<Ingredient> ingredients = ingredientService.getIngredientsInExportOrder();
        assertTrue(!categories.isEmpty() && !ingredients.isEmpty());

        String recipeJson = "{"
                + "\"name\":\"PermRecipeBlocked_" + UUID.randomUUID() + "\","
                + "\"ownerId\":" + user.user.getId() + ","
                + "\"description\":\"test\","
                + "\"productionSteps\":[{"
                + "\"type\":\"addIngredients\","
                + "\"stepIngredients\":[{"
                + "\"ingredientId\":" + ingredients.get(0).getId() + ","
                + "\"amount\":10,"
                + "\"scale\":false,"
                + "\"boostable\":false"
                + "}]"
                + "}],"
                + "\"categoryIds\":[" + categories.get(0).getId() + "]"
                + "}";

        MockMultipartFile recipePart = new MockMultipartFile(
                "recipe",
                "recipe.json",
                MediaType.APPLICATION_JSON_VALUE,
                recipeJson.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/recipe/")
                        .file(recipePart)
                        .header("Authorization", bearer(user.token))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden());
    }

    @Test
    void pumpEditorCanPatchPumpUserCannot() throws Exception {
        AuthUser pumpEditor = createUserToken(ERole.ROLE_PUMP_INGREDIENT_EDITOR);
        AuthUser user = createUserToken(ERole.ROLE_USER);
        Pump pump = pumpService.getAllPumps().get(0);

        PumpDto.Request.Create dto = PumpDto.Request.Create.toDto(pump);
        String payload = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/api/pump/{id}", pump.getId())
                        .header("Authorization", bearer(user.token))
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/pump/{id}", pump.getId())
                        .header("Authorization", bearer(pumpEditor.token))
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());
    }

    @Test
    void adminCanListUsersUserCannot() throws Exception {
        AuthUser admin = createUserToken(ERole.ROLE_ADMIN);
        AuthUser user = createUserToken(ERole.ROLE_USER);

        mockMvc.perform(get("/api/user/")
                        .header("Authorization", bearer(admin.token)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/user/")
                        .header("Authorization", bearer(user.token)))
                .andExpect(status().isForbidden());
    }

    @Test
    void superAdminOnlyEndpoints() throws Exception {
        String superToken = getAdminToken();
        AuthUser admin = createUserToken(ERole.ROLE_ADMIN);

        mockMvc.perform(get("/api/gpio/")
                        .header("Authorization", bearer(admin.token)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/gpio/")
                        .header("Authorization", bearer(superToken)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/eventaction/")
                        .header("Authorization", bearer(admin.token)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/eventaction/")
                        .header("Authorization", bearer(superToken)))
                .andExpect(status().isOk());
    }
}
