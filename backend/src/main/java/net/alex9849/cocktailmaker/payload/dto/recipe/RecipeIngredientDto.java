package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class RecipeIngredientDto {

    @NotNull
    private IngredientDto ingredient;

    @Min(1)
    private int amount;

    private boolean scale;

    public RecipeIngredientDto() {}

    public RecipeIngredientDto(RecipeIngredient recipeIngredient) {
        if(recipeIngredient.getIngredient() != null) {
            this.ingredient = IngredientDto.toDto(recipeIngredient.getIngredient());
        } else {
            this.ingredient = null;
        }
        this.scale = recipeIngredient.isScale();
        this.amount = recipeIngredient.getAmount();
    }

    public IngredientDto getIngredient() {
        return ingredient;
    }

    public void setIngredient(IngredientDto ingredient) {
        this.ingredient = ingredient;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isScale() {
        return scale;
    }

    public void setScale(boolean scale) {
        this.scale = scale;
    }
}
