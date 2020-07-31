package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.security.services.UserDetailsImpl;
import net.alex9849.cocktailmaker.service.RecipeService;
import net.alex9849.cocktailmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipe")
public class RecipeEndpoint {

    @Autowired
    RecipeService recipeService;

    @Autowired
    UserService userService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    ResponseEntity<?> getRecipesByFilter(@RequestParam(value = "ownerId", required = false) Long ownerId,
            @RequestParam(value = "inPublic", required = false) Boolean inPublic,
            @RequestParam(value = "searchName", required = false) String searchName) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!userDetails.getId().equals(ownerId) && (inPublic == null || !inPublic)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok().body(recipeService.getRecipesByFilter(ownerId, inPublic, searchName).stream()
                .map(RecipeDto::new).collect(Collectors.toList()));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    ResponseEntity<?> getRecipe(@PathVariable("id") long id, HttpServletRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.getById(id);
        if(recipe == null) {
            return ResponseEntity.notFound().build();
        }
        if(!recipe.getOwner().getId().equals(userDetails.getId()) && !recipe.isInPublic()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new RecipeDto(recipe));
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    ResponseEntity<?> createRecipe(@Valid @RequestBody RecipeDto recipeDto, UriComponentsBuilder uriBuilder) {
        recipeDto.setId(null);
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.fromDto(recipeDto);
        recipe.setOwner(userService.getUser(userDetails.getId()));
        recipe = recipeService.createRecipe(recipe);
        UriComponents uriComponents = uriBuilder.path("/api/recipe/{id}").buildAndExpand(recipe.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(new RecipeDto(recipe));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.PUT)
    ResponseEntity<?> updateRecipe(@Valid @RequestBody RecipeDto recipeDto, @PathVariable("id") long id, HttpServletRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        recipeDto.setId(id);
        Recipe recipe = recipeService.fromDto(recipeDto);
        Recipe oldRecipe = recipeService.getById(id);
        if(oldRecipe == null) {
            return ResponseEntity.notFound().build();
        }
        recipe.setOwner(oldRecipe.getOwner());
        if(!recipe.getOwner().getId().equals(userDetails.getId()) && !request.isUserInRole("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        recipeService.updateRecipe(recipe);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    ResponseEntity<?> deleteRecipe(@PathVariable("id") long id, HttpServletRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.getById(id);
        if(recipe == null) {
            return ResponseEntity.notFound().build();
        }
        if(!recipe.getOwner().getId().equals(userDetails.getId()) && !request.isUserInRole("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        recipeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
