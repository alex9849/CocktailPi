package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.ProductionStepIngredient;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ProductionStepIngredientDto {
    @NotNull
    private IngredientDto ingredient;

    @Min(1)
    private int amount;
    private boolean scale;

    public ProductionStepIngredientDto(ProductionStepIngredient productionStepIngredient) {
        BeanUtils.copyProperties(productionStepIngredient, this);
        ingredient = IngredientDto.toDto(productionStepIngredient.getIngredient());
    }

    public ProductionStepIngredientDto() {}

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
