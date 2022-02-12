package net.alex9849.cocktailmaker.payload.dto.cocktail;

import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.ProductionStepIngredientDto;

import java.util.List;
import java.util.stream.Collectors;

public class CocktailprogressDto {
    private RecipeDto.Response.Detailed recipe;
    private int progress;
    private String user;
    private Cocktailprogress.State state;
    private List<ProductionStepIngredientDto.Response.Detailed> currentIngredientsToAddManually;
    private String writtenInstruction;


    public CocktailprogressDto(Cocktailprogress cocktailprogress) {
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

    public CocktailprogressDto() {}

    public RecipeDto.Response.Detailed getRecipe() {
        return recipe;
    }

    public void setRecipe(RecipeDto.Response.Detailed recipe) {
        this.recipe = recipe;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getWrittenInstruction() {
        return writtenInstruction;
    }

    public void setWrittenInstruction(String writtenInstruction) {
        this.writtenInstruction = writtenInstruction;
    }

    public Cocktailprogress.State getState() {
        return state;
    }

    public void setState(Cocktailprogress.State state) {
        this.state = state;
    }

    public List<ProductionStepIngredientDto.Response.Detailed> getCurrentIngredientsToAddManually() {
        return currentIngredientsToAddManually;
    }

    public void setCurrentIngredientsToAddManually(List<ProductionStepIngredientDto.Response.Detailed> currentIngredientsToAddManually) {
        this.currentIngredientsToAddManually = currentIngredientsToAddManually;
    }
}
