package net.alex9849.cocktailmaker.payload.dto.cocktail;

import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.payload.dto.user.UserDto;

public class CocktailprogressDto {
    private RecipeDto recipe;
    private int progress;
    private UserDto user;
    private boolean isCanceled;

    public CocktailprogressDto(Cocktailprogress cocktailprogress) {
        this.progress = cocktailprogress.getProgress();
        this.isCanceled = cocktailprogress.isCanceled();
        if(cocktailprogress.getRecipe() != null) {
            this.recipe = new RecipeDto(cocktailprogress.getRecipe());
            this.recipe.setRecipeIngredients(null);
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

    public boolean isDone() {
        return this.progress >= 100;
    }

    public boolean isCanceled() {
        return isCanceled;
    }
}
