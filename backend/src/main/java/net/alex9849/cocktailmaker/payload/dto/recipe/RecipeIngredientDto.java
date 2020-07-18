package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;

import javax.validation.constraints.NotNull;

public class RecipeIngredientDto {

    @NotNull
    private IngredientDto ingredient;

    private int amount;

    public RecipeIngredientDto() {}

    public RecipeIngredientDto(RecipeIngredient recipeIngredient) {
        this.ingredient = new IngredientDto(recipeIngredient.getIngredient());
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
}
