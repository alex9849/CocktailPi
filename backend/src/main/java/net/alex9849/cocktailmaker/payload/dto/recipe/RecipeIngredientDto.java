package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;

import javax.validation.constraints.NotNull;

public class RecipeIngredientDto {

    @NotNull
    private IngredientDto ingredient;

    private int amount;

    private int productionStep;

    public RecipeIngredientDto() {}

    public RecipeIngredientDto(RecipeIngredient recipeIngredient) {
        if(recipeIngredient.getIngredient() != null) {
            this.ingredient = new IngredientDto(recipeIngredient.getIngredient());
        } else {
            this.ingredient = null;
        }

        this.amount = recipeIngredient.getAmount();
        this.productionStep = recipeIngredient.getId().getProductionStep();
    }

    public int getProductionStep() {
        return productionStep;
    }

    public void setProductionStep(int productionStep) {
        this.productionStep = productionStep;
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
