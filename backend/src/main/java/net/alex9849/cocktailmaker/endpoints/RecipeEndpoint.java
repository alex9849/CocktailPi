package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipe")
public class RecipeEndpoint {

    @Autowired
    RecipeService recipeService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    ResponseEntity<?> getRecipes() {
        return ResponseEntity.ok().body(recipeService.getAll());
    }

    @RequestMapping(path = {"{userId}", "own"}, method = RequestMethod.GET)
    ResponseEntity<?> getRecipeByUser(@PathVariable(value = "userId", required = false) Long userId) {
        return null;
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    ResponseEntity<?> getRecipe(@PathVariable("id") long id) {
        return null;
    }
}
