package db.migration;

import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.service.CategoryService;
import net.alex9849.cocktailmaker.service.IngredientService;
import net.alex9849.cocktailmaker.service.RecipeService;
import net.alex9849.cocktailmaker.service.UserService;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class R__Insert_Default_Data extends BaseJavaMigration {

    private final UserService userService = SpringUtility.getBean(UserService.class);
    private final RecipeService recipeService = SpringUtility.getBean(RecipeService.class);
    private final CategoryService categoryService = SpringUtility.getBean(CategoryService.class);
    private final IngredientService ingredientService = SpringUtility.getBean(IngredientService.class);

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


}
