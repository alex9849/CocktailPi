package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.payload.dto.recipe.IngredientDto;
import net.alex9849.cocktailmaker.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ingredient")
public class IngredientEndpoint {

    @Autowired
    IngredientService ingredientService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    ResponseEntity<?> getIngredients(@RequestParam(value = "autocomplete", required = false) String autocomplete,
                                     @RequestParam(value = "filterManualIngredients", defaultValue = "false") boolean filterManualIngredients) {
        if(autocomplete == null){
            if(!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(ERole.ROLE_ADMIN)) {
                ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            if(autocomplete.length() < 2) {
                throw new IllegalArgumentException("Autocomplete too short");
            }
        }
        return ResponseEntity.ok(ingredientService.getIngredientByFilter(autocomplete, filterManualIngredients, false)
                .stream().map(IngredientDto::toDto).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<?> createIngredient(@Valid @RequestBody IngredientDto ingredientDto, UriComponentsBuilder uriBuilder) {
        Ingredient ingredient = ingredientService.fromDto(ingredientDto);
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
        if(!ingredientService.deleteIngredient(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "{id}/bar", method = RequestMethod.PUT)
    ResponseEntity<?> addToBar(@PathVariable("id") long id) {
        ingredientService.setInBar(id, true);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "{id}/bar", method = RequestMethod.DELETE)
    ResponseEntity<?> removeFromBar(@PathVariable("id") long id) {
        ingredientService.setInBar(id, false);
        return ResponseEntity.ok().build();
    }

}
