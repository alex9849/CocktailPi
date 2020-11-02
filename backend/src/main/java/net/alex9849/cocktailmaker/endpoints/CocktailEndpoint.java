package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.service.PumpService;
import net.alex9849.cocktailmaker.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/cocktail")
public class CocktailEndpoint {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private PumpService pumpService;

    @RequestMapping(value = "{recipeId}", method = RequestMethod.PUT)
    public ResponseEntity<?> orderCocktail(@PathVariable("recipeId") long recipeId, @RequestParam(value = "amount", defaultValue = "250") int amount) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.getById(recipeId);
        if(recipe == null) {
            return ResponseEntity.notFound().build();
        }
        if(!recipe.isInPublic() && !Objects.equals(user.getId(), recipe.getOwner().getId()) && !user.getAuthorities().contains(ERole.ROLE_ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        pumpService.orderCocktail(user, recipe, amount);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<?> cancelCocktail() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cocktailprogress progress = pumpService.getCurrentCocktailProgress();
        if(progress == null) {
            return ResponseEntity.notFound().build();
        }
        if(!Objects.equals(progress.getUser().getId(), user.getId()) && !user.getAuthorities().contains(ERole.ROLE_ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(!pumpService.cancelCocktailOrder()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

}
