package net.alex9849.cocktailmaker.payload.dto.cocktail;

import lombok.*;
import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.ProductionStepIngredientDto;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CocktailprogressDto {
    public interface Recipe { RecipeDto.Response.Detailed getRecipe(); }
    public interface Progress { int getProgress(); }
    public interface User { String getUser(); }
    public interface State { Cocktailprogress.State getState(); }
    public interface CurrentIngredientsToAddManually { List<ProductionStepIngredientDto.Response.Detailed> getCurrentIngredientsToAddManually(); }
    public interface WrittenInstruction { String getWrittenInstruction(); }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements Recipe, Progress, User, State, CurrentIngredientsToAddManually, WrittenInstruction {
            RecipeDto.Response.Detailed recipe;
            int progress;
            String user;
            Cocktailprogress.State state;
            List<ProductionStepIngredientDto.Response.Detailed> currentIngredientsToAddManually;
            String writtenInstruction;

            public Detailed(Cocktailprogress cocktailprogress) {
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
                    this.recipe = new RecipeDto.Response.Detailed(cocktailprogress.getRecipe());
                    this.recipe.setProductionSteps(null);
                }
                if(cocktailprogress.getUser() != null) {
                    this.user = cocktailprogress.getUser().getUsername();
                }
            }
        }
    }
}
