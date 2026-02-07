package net.alex9849.cocktailpi;

import com.fasterxml.jackson.databind.JsonNode;
import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.model.Glass;
import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.service.CategoryService;
import net.alex9849.cocktailpi.service.GlassService;
import net.alex9849.cocktailpi.service.GpioService;
import net.alex9849.cocktailpi.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EndpointIntegrationTest extends BackendIntegrationTestBase {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private GlassService glassService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private GpioService gpioService;

    private String authHeader() {
        return "Bearer " + getAdminToken();
    }

    @Test
    void refreshTokenReturnsJwt() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/refreshToken")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        assertTrue(node.hasNonNull("accessToken"));
    }

    @Test
    void categoryCrudRoundTrip() throws Exception {
        String payload = """
                {"name":"TestCategory"}
                """;
        MvcResult createResult = mockMvc.perform(post("/api/category/")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long id = created.get("id").asLong();

        mockMvc.perform(get("/api/category/{id}", id)
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/category/{id}", id)
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void collectionCreateAndFetch() throws Exception {
        String payload = """
                {"name":"TestCollection","description":"Test collection description"}
                """;
        MvcResult createResult = mockMvc.perform(post("/api/collection/")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long id = created.get("id").asLong();

        mockMvc.perform(get("/api/collection/{id}", id)
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void getGlassesAndSingleGlass() throws Exception {
        List<Glass> glasses = glassService.getAll();
        assertTrue(glasses.size() > 0, "Expected seed glasses");

        mockMvc.perform(get("/api/glass/")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/glass/{id}", glasses.get(0).getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void getGpioBoardsAndPins() throws Exception {
        List<GpioBoard> boards = gpioService.getGpioBoards();
        assertTrue(boards.size() > 0, "Expected seed gpio board");

        mockMvc.perform(get("/api/gpio/")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/gpio/{id}/pin", boards.get(0).getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void getIngredients() throws Exception {
        mockMvc.perform(get("/api/ingredient/")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void getPumps() throws Exception {
        mockMvc.perform(get("/api/pump/")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void getPumpSettings() throws Exception {
        mockMvc.perform(get("/api/pump/settings/reversepumping")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/pump/settings/powerlimit")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void getRecipesAndSingleRecipe() throws Exception {
        List<Recipe> recipes = recipeService.getAll();
        assertTrue(recipes.size() > 0, "Expected seed recipes");

        mockMvc.perform(get("/api/recipe/")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/recipe/{id}", recipes.get(0).getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void getIngredientRecipes() throws Exception {
        mockMvc.perform(get("/api/recipe/ingredient")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void cocktailFeasibilityCheck() throws Exception {
        Recipe recipe = recipeService.getAll().stream().findFirst().orElse(null);
        assertNotNull(recipe);

        String payload = """
                {
                  "amountOrderedInMl": 200,
                  "ingredientGroupReplacements": [],
                  "customisations": { "boost": 100, "additionalIngredients": [] }
                }
                """;

        mockMvc.perform(put("/api/cocktail/{id}/feasibility", recipe.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());
    }

    @Test
    void getSystemSettingsAndVersion() throws Exception {
        mockMvc.perform(get("/api/system/settings/appearance")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/system/settings/global")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/system/version")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void getTransferRecipesExport() throws Exception {
        mockMvc.perform(post("/api/transfer/export/recipes")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void getUsersAndCurrentUser() throws Exception {
        mockMvc.perform(get("/api/user/")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/user/current")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void getEventActionsAndExecutionGroups() throws Exception {
        mockMvc.perform(get("/api/eventaction/")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/eventaction/executiongroup")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void getCategoriesAndById() throws Exception {
        List<Category> categories = categoryService.getAllCategories();
        assertTrue(categories.size() > 0, "Expected seed categories");

        mockMvc.perform(get("/api/category/")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/category/{id}", categories.get(0).getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk());
    }
}
