package net.alex9849.cocktailmaker.payload.dto.cocktail;

import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.payload.dto.user.UserDto;
import org.springframework.beans.BeanUtils;

public class CocktailprogressDto {
    private RecipeDto recipe;
    private int progress;
    private UserDto user;
    private boolean aborted;

    public CocktailprogressDto(Cocktailprogress cocktailprogress) {
        BeanUtils.copyProperties(cocktailprogress, this);
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

    public boolean isAborted() {
        return aborted;
    }

    public void setAborted(boolean aborted) {
        this.aborted = aborted;
    }
}
