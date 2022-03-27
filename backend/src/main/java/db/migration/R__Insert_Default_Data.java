package db.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.alex9849.cocktailmaker.model.Category;
import net.alex9849.cocktailmaker.model.recipe.*;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.repository.CategoryRepository;
import net.alex9849.cocktailmaker.repository.IngredientRepository;
import net.alex9849.cocktailmaker.repository.RecipeRepository;
import net.alex9849.cocktailmaker.repository.UserRepository;
import net.alex9849.cocktailmaker.service.RecipeService;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class R__Insert_Default_Data extends BaseJavaMigration {

    private final UserRepository userRepository = SpringUtility.getBean(UserRepository.class);
    private final RecipeService recipeService = SpringUtility.getBean(RecipeService.class);
    private final RecipeRepository recipeRepository = SpringUtility.getBean(RecipeRepository.class);
    private final CategoryRepository categoryRepository = SpringUtility.getBean(CategoryRepository.class);
    private final IngredientRepository ingredientRepository = SpringUtility.getBean(IngredientRepository.class);

    private Map<String, Category> categoriesByName = null;
    private Map<String, Ingredient> ingredientsByName = null;

    @Override
    public void migrate(Context context) throws Exception {
        /*

        Connection connection = context.getConnection();
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(connection, true));
        userRepository.setJdbcTemplate(jdbcTemplate);
        recipeRepository.setJdbcTemplate(jdbcTemplate);
        categoryRepository.setJdbcTemplate(jdbcTemplate);
        ingredientRepository.setJdbcTemplate(jdbcTemplate);

        if(!userRepository.findAll().isEmpty()) {
            return;
        }

         */

        InputStream recipeStream = this.getClass().getResourceAsStream("/db/defaultdata/recipes.json");
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeReference<List<RecipeDto.Response.Detailed>> typeReference = new TypeReference<>(){};
        List<RecipeDto.Response.Detailed> recipeDtos = mapper.readValue(recipeStream, typeReference);
        User defaultUser = createDefaultUser();
        for(RecipeDto.Response.Detailed recipeDto : recipeDtos) {
            /*
            recipeDto.setOwnerId(defaultUser.getId());
            Recipe recipe = recipeService.fromDto(recipeDto);
            recipe.setOwner(defaultUser);
            recipe.setOwnerId(defaultUser.getId());
            long recipeId = createRecipe(recipe).getId();
            InputStream recipeImageStream = this.getClass().getResourceAsStream("/db/defaultdata/images/" + recipeDto.getName() + ".jpg");
            if(recipeImageStream != null) {
                BufferedImage image = ImageIO.read(recipeImageStream);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", out);
                recipeRepository.setImage(recipeId, out.toByteArray());
            }

             */
        }
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
        return userRepository.create(defaultUser);
    }

    private Recipe createRecipe(Recipe recipe) {
        for(ProductionStep pStep : recipe.getProductionSteps()) {
            if(pStep instanceof AddIngredientsProductionStep) {
                AddIngredientsProductionStep addIPStep = (AddIngredientsProductionStep) pStep;
                for(ProductionStepIngredient psi : addIPStep.getStepIngredients()) {
                    psi.setIngredient(getOrCreateIngredient(psi.getIngredient()));
                }
            }
        }
        for(int i = 0; i < recipe.getCategories().size(); i++) {
            recipe.getCategories().set(i, getOrCreateCategory(recipe.getCategories().get(i)));
        }
        return recipeRepository.create(recipe);
    }

    private Ingredient getOrCreateIngredient(Ingredient searchIngredient) {
        if(ingredientsByName == null) {
            ingredientsByName = ingredientRepository.findAll()
                    .stream().collect(Collectors.toMap(Ingredient::getName, x -> x));
        }
        if(!ingredientsByName.containsKey(searchIngredient.getName())) {
            Ingredient createdIngredient = ingredientRepository.create(searchIngredient);
            ingredientsByName.put(createdIngredient.getName(), createdIngredient);
        }
        return ingredientsByName.get(searchIngredient.getName());
    }

    private Category getOrCreateCategory(Category searchCategory) {
        if(categoriesByName == null) {
            categoriesByName = categoryRepository.findAll().stream()
                    .collect(Collectors.toMap(Category::getName, x -> x));
        }
        if(!categoriesByName.containsKey(searchCategory.getName())) {
            Category createdCategory = categoryRepository.create(searchCategory);
            categoriesByName.put(createdCategory.getName(), createdCategory);
        }
        return categoriesByName.get(searchCategory.getName());
    }


}
