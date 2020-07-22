package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.service.IngredientService;
import net.alex9849.cocktailmaker.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingredient")
public class IngredientEndpoint {

    @Autowired
    RecipeService recipeService;

    @Autowired
    IngredientService ingredientService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    ResponseEntity<?> getIngredients(@RequestParam(value = "autocomplete", required = false) String autocomplete) {
        return ResponseEntity.ok(ingredientService.getIngredientByFilter(autocomplete));
    }
}
