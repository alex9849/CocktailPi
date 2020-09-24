package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.service.CocktailFactoryService;
import net.alex9849.cocktailmaker.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/cocktail")
public class CocktailEndpoint {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CocktailFactoryService cocktailFactoryService;

    @RequestMapping(value = "{recipeId}", method = RequestMethod.PUT)
    public ResponseEntity<?> orderCocktail(@PathVariable("recipeId") long recipeId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.getById(recipeId);
        if(recipe == null) {
            return ResponseEntity.notFound().build();
        }
        if(!Objects.equals(user.getId(), recipe.getOwner().getId()) && !user.getAuthorities().contains(ERole.ROLE_ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        cocktailFactoryService.orderCocktail(user, recipe);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> cancelCocktail() {
        return ResponseEntity.ok().build();
    }

}
