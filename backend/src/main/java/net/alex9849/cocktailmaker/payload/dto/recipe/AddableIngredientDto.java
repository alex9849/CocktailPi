package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.AddableIngredient;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public abstract class AddableIngredientDto extends IngredientDto {
    @Min(0) @Max(100)
    private int alcoholContent;
    private boolean inBar;

    public AddableIngredientDto() {}

    public AddableIngredientDto(AddableIngredient ingredient) {
        BeanUtils.copyProperties(ingredient, this);
    }

    public int getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(int alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    @Override
    public boolean isInBar() {
        return this.inBar;
    }
}
