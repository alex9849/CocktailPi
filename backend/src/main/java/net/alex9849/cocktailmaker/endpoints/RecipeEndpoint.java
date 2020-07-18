package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipe")
public class RecipeEndpoint {

    @Autowired
    RecipeService recipeService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    ResponseEntity<?> getRecipes(@RequestParam(value = "userId", required = false) Long userId) {
        if(userId != null) {
            return ResponseEntity.ok().body(recipeService.getByOwner(userId).stream()
                    .map(RecipeDto::new).collect(Collectors.toList()));
        }
        return ResponseEntity.ok().body(recipeService.getAll().stream()
                .map(RecipeDto::new).collect(Collectors.toList()));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    ResponseEntity<?> getRecipe(@PathVariable("id") long id) {
        Recipe recipe = recipeService.getById(id);
        if(recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new RecipeDto(recipe));
    }
}
