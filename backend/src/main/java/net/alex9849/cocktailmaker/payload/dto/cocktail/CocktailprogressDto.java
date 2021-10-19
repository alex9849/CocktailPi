package net.alex9849.cocktailmaker.payload.dto.cocktail;

import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeIngredientDto;
import net.alex9849.cocktailmaker.payload.dto.user.UserDto;

import java.util.List;
import java.util.stream.Collectors;

public class CocktailprogressDto {
    private RecipeDto recipe;
    private int progress;
    private UserDto user;
    private Cocktailprogress.State state;
    private List<RecipeIngredientDto> currentIngredientsToAddManually;

    public CocktailprogressDto(Cocktailprogress cocktailprogress) {
        this.progress = cocktailprogress.getProgress();
        this.state = cocktailprogress.getState();
        if(cocktailprogress.getCurrentIngredientsToAddManually() != null) {
            this.currentIngredientsToAddManually = cocktailprogress.getCurrentIngredientsToAddManually()
                    .stream()
                    .map(RecipeIngredientDto::new)
                    .collect(Collectors.toList());
        }
        if(cocktailprogress.getRecipe() != null) {
            this.recipe = new RecipeDto(cocktailprogress.getRecipe());
            this.recipe.setProductionSteps(null);
        }
        if(cocktailprogress.getUser() != null) {
            UserDto userDto = new UserDto();
            userDto.setId(cocktailprogress.getUser().getId());
            userDto.setUsername(cocktailprogress.getUser().getUsername());
            this.user = userDto;
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

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public Cocktailprogress.State getState() {
        return state;
    }

    public void setState(Cocktailprogress.State state) {
        this.state = state;
    }

    public List<RecipeIngredientDto> getCurrentIngredientsToAddManually() {
        return currentIngredientsToAddManually;
    }

    public void setCurrentIngredientsToAddManually(List<RecipeIngredientDto> currentIngredientsToAddManually) {
        this.currentIngredientsToAddManually = currentIngredientsToAddManually;
    }
}
