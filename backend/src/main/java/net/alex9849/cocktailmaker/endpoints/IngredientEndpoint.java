package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.payload.dto.recipe.IngredientDto;
import net.alex9849.cocktailmaker.service.IngredientService;
import net.alex9849.cocktailmaker.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/ingredient")
public class IngredientEndpoint {

    @Autowired
    RecipeService recipeService;

    @Autowired
    IngredientService ingredientService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    ResponseEntity<?> getIngredients(@RequestParam(value = "autocomplete", required = false) String autocomplete) {
        return ResponseEntity.ok(ingredientService.getIngredientByFilter(autocomplete));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> createIngredient(@Valid @RequestBody IngredientDto ingredientDto, UriComponentsBuilder uriBuilder) {
        Ingredient ingredient = ingredientService.fromDto(ingredientDto);
        ingredient.setId(null);
        ingredient = ingredientService.createIngredient(ingredient);
        UriComponents uriComponents = uriBuilder.path("/api/ingredient/{id}").buildAndExpand(ingredient.getId());
        return ResponseEntity.ok(uriComponents.toUri());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    ResponseEntity<?> updateIngredient(@Valid @PathVariable("id") long id, @RequestBody IngredientDto ingredientDto) {
        if(ingredientService.getIngredient(id) == null) {
            return ResponseEntity.notFound().build();
        }
        Ingredient ingredient = ingredientService.fromDto(ingredientDto);
        ingredient.setId(id);
        ingredientService.updateIngredient(ingredient);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    ResponseEntity<?> deleteIngredient(@PathVariable("id") long id) {
        Ingredient ingredient = ingredientService.getIngredient(id);
        if(ingredient == null) {
            return ResponseEntity.notFound().build();
        }
        ingredientService.deleteIngredient(ingredient);
        return ResponseEntity.ok().build();
    }

}
