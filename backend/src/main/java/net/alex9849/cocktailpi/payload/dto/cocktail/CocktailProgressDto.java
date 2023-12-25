package net.alex9849.cocktailpi.payload.dto.cocktail;

import lombok.*;
import net.alex9849.cocktailpi.model.cocktail.CocktailProgress;
import net.alex9849.cocktailpi.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailpi.payload.dto.recipe.productionstep.ProductionStepIngredientDto;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CocktailProgressDto {
    public interface Recipe { RecipeDto.Response.SearchResult getRecipe(); }
    public interface Progress { int getProgress(); }
    public interface State { CocktailProgress.State getState(); }
    public interface CurrentIngredientsToAddManually { List<ProductionStepIngredientDto.Response.Detailed> getCurrentIngredientsToAddManually(); }
    public interface WrittenInstruction { String getWrittenInstruction(); }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements Recipe, Progress, State, CurrentIngredientsToAddManually, WrittenInstruction {
            RecipeDto.Response.SearchResult recipe;
            int progress;
            long userId;
            CocktailProgress.State state;
            List<ProductionStepIngredientDto.Response.Detailed> currentIngredientsToAddManually;
            String writtenInstruction;

            public Detailed(CocktailProgress cocktailprogress) {
                this.progress = cocktailprogress.getProgress();
                this.state = cocktailprogress.getState();
                if(cocktailprogress.getCurrentIngredientsToAddManually() != null) {
                    this.currentIngredientsToAddManually = cocktailprogress.getCurrentIngredientsToAddManually()
                            .stream()
                            .map(ProductionStepIngredientDto.Response.Detailed::new)
                            .collect(Collectors.toList());
                }
                if(cocktailprogress.getWrittenInstruction() != null) {
                    this.writtenInstruction = cocktailprogress.getWrittenInstruction();
                }
                if(cocktailprogress.getRecipe() != null) {
                    this.recipe = RecipeDto.Response.SearchResult.toDto(cocktailprogress.getRecipe());
                }
                if(cocktailprogress.getUser() != null) {
                    this.userId = cocktailprogress.getUser().getId();
                }
            }
        }
    }
}
