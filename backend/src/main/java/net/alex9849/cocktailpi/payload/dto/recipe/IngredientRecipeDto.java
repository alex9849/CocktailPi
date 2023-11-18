package net.alex9849.cocktailpi.payload.dto.recipe;

import lombok.*;
import net.alex9849.cocktailpi.model.recipe.IngredientRecipe;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.IngredientDto;

public class IngredientRecipeDto {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends RecipeDto.Response.Detailed {
            IngredientDto.Response.Detailed ingredient;
            long mlLeft;

            public Detailed(IngredientRecipe ingredientRecipe) {
                super(ingredientRecipe);
                this.mlLeft = ingredientRecipe.getMlLeft();
                this.ingredient = IngredientDto.Response.Detailed.toDto(ingredientRecipe.getIngredient());
            }

            public String getType() {
                return "ingredientrecipe";
            }
        }

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class SearchResult extends RecipeDto.Response.SearchResult {
            IngredientDto.Response.Reduced ingredient;
            long mlLeft;

            public SearchResult(IngredientRecipe ingredientRecipe) {
                super(ingredientRecipe);
                this.mlLeft = ingredientRecipe.getMlLeft();
                this.ingredient = IngredientDto.Response.Reduced.toDto(ingredientRecipe.getIngredient());
            }

            public String getType() {
                return "ingredientrecipe";
            }
        }
    }

}
