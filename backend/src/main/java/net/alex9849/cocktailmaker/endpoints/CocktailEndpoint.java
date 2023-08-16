package net.alex9849.cocktailmaker.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailmaker.model.cocktail.CocktailProgress;
import net.alex9849.cocktailmaker.model.recipe.CocktailOrderConfiguration;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.cocktail.CocktailOrderConfigurationDto;
import net.alex9849.cocktailmaker.payload.dto.cocktail.FeasibilityReportDto;
import net.alex9849.cocktailmaker.service.PumpService;
import net.alex9849.cocktailmaker.service.RecipeService;
import net.alex9849.cocktailmaker.service.pumps.CocktailOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/cocktail/")
public class CocktailEndpoint {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CocktailOrderService cocktailOrderService;

    @Autowired
    private PumpService pumpService;

    @RequestMapping(value = "{recipeId}", method = RequestMethod.PUT)
    public ResponseEntity<?> orderCocktail(@PathVariable("recipeId") long recipeId, @Valid @RequestBody CocktailOrderConfigurationDto.Request.Create orderConfigDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.getById(recipeId);
        if(recipe == null) {
            return ResponseEntity.notFound().build();
        }
        if(orderConfigDto.getAmountOrderedInMl() == null) {
            orderConfigDto.setAmountOrderedInMl((int) recipe.getDefaultAmountToFill());
        }
        CocktailOrderConfiguration orderConfig = cocktailOrderService.fromDto(orderConfigDto);
        pumpService.orderCocktail(user, recipe, orderConfig);
        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "{recipeId}/feasibility", method = RequestMethod.PUT)
    public ResponseEntity<?> checkFeasibility(@PathVariable("recipeId") long recipeId, @Valid @RequestBody CocktailOrderConfigurationDto.Request.Create orderConfigDto) {
        Recipe recipe = recipeService.getById(recipeId);
        if(recipe == null) {
            return ResponseEntity.notFound().build();
        }
        if(orderConfigDto.getAmountOrderedInMl() == null) {
            orderConfigDto.setAmountOrderedInMl((int) recipe.getDefaultAmountToFill());
        }
        CocktailOrderConfiguration orderConfig = cocktailOrderService.fromDto(orderConfigDto);
        return ResponseEntity.ok(new FeasibilityReportDto.Response.Detailed(cocktailOrderService.checkFeasibility(recipe, orderConfig).getFeasibilityReport()));
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<?> cancelCocktail() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CocktailProgress progress = cocktailOrderService.getCurrentCocktailProgress();
        if(progress == null) {
            return ResponseEntity.notFound().build();
        }
        if(!Objects.equals(progress.getUser().getId(), user.getId()) && !user.getAuthorities().contains(ERole.ROLE_ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(!cocktailOrderService.cancelCocktailOrder()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "continueproduction", method = RequestMethod.POST)
    public ResponseEntity<?> continueCocktailProduction() {
        cocktailOrderService.continueCocktailProduction();
        return ResponseEntity.accepted().build();
    }

}
