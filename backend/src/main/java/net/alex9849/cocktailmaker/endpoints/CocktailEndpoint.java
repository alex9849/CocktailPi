package net.alex9849.cocktailmaker.endpoints;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cocktail")
public class CocktailEndpoint {
/*
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
*/
}
