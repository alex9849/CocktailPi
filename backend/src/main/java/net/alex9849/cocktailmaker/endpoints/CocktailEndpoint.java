package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.cocktail.CocktailProgress;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.cocktail.FeasibilityReportDto;
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
    public ResponseEntity<?> orderCocktail(@PathVariable("recipeId") long recipeId, @RequestParam(value = "amount", required = false) Integer amount) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.getById(recipeId);
        if(recipe == null) {
            return ResponseEntity.notFound().build();
        }
        if(amount == null) {
            amount = (int) recipe.getDefaultAmountToFill();
        } else if (amount < 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        pumpService.orderCocktail(user, recipe, amount);
        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "{recipeId}/feasibility", method = RequestMethod.GET)
    public ResponseEntity<?> checkFeasibility(@PathVariable("recipeId") long recipeId, @RequestParam(value = "amount", required = false) Integer amount) {
        Recipe recipe = recipeService.getById(recipeId);
        if(recipe == null) {
            return ResponseEntity.notFound().build();
        }
        if(amount == null) {
            amount = (int) recipe.getDefaultAmountToFill();
        } else if (amount < 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(new FeasibilityReportDto.Response.Detailed(pumpService.checkFeasibility(recipe, amount)));
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<?> cancelCocktail() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CocktailProgress progress = pumpService.getCurrentCocktailProgress();
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

    @RequestMapping(value = "continueproduction", method = RequestMethod.POST)
    public ResponseEntity<?> continueCocktailProduction() {
        pumpService.continueCocktailProduction();
        return ResponseEntity.accepted().build();
    }

}
