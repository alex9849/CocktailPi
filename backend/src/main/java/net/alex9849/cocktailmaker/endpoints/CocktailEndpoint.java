package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.security.services.UserDetailsImpl;
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
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.getById(recipeId);
        if(!Objects.equals(userDetails.getId(), recipe.getOwner().getId()) && !userDetails.getAuthorities().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            cocktailFactoryService.orderCocktail(userDetails, recipe);
        } catch (...)
    }

    public ResponseEntity<?> cancelCocktail() {

    }

}
