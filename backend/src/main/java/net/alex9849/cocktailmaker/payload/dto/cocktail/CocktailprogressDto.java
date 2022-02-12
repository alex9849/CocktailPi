package net.alex9849.cocktailmaker.payload.dto.cocktail;

import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.payload.dto.OwnerDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.ProductionStepIngredientDto;

import java.util.List;
import java.util.stream.Collectors;

public class CocktailprogressDto {
    private RecipeDto recipe;
    private int progress;
    private OwnerDto user;
    private Cocktailprogress.State state;
    private List<ProductionStepIngredientDto> currentIngredientsToAddManually;
    private String writtenInstruction;


    public CocktailprogressDto(Cocktailprogress cocktailprogress) {
        this.progress = cocktailprogress.getProgress();
        this.state = cocktailprogress.getState();
        if(cocktailprogress.getCurrentIngredientsToAddManually() != null) {
            this.currentIngredientsToAddManually = cocktailprogress.getCurrentIngredientsToAddManually()
                    .stream()
                    .map(ProductionStepIngredientDto::new)
                    .collect(Collectors.toList());
        }
        if(cocktailprogress.getWrittenInstruction() != null) {
            this.writtenInstruction = cocktailprogress.getWrittenInstruction();
        }
        if(cocktailprogress.getRecipe() != null) {
            this.recipe = new RecipeDto(cocktailprogress.getRecipe());
            this.recipe.setProductionSteps(null);
        }
        if(cocktailprogress.getUser() != null) {
            OwnerDto ownerDto = new OwnerDto();
            ownerDto.setId(cocktailprogress.getUser().getId());
            ownerDto.setUsername(cocktailprogress.getUser().getUsername());
            this.user = ownerDto;
        }
    }

    public CocktailprogressDto() {}

    public RecipeDto getRecipe() {
        return recipe;
    }

    public void setRecipe(RecipeDto recipe) {
        this.recipe = recipe;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public OwnerDto getUser() {
        return user;
    }

    public void setUser(OwnerDto user) {
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

    public List<ProductionStepIngredientDto> getCurrentIngredientsToAddManually() {
        return currentIngredientsToAddManually;
    }

    public void setCurrentIngredientsToAddManually(List<ProductionStepIngredientDto> currentIngredientsToAddManually) {
        this.currentIngredientsToAddManually = currentIngredientsToAddManually;
    }
}
