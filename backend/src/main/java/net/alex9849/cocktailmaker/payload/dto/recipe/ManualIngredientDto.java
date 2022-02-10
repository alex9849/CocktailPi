package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ManualIngredient;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;

public class ManualIngredientDto extends AddableIngredientDto {

    @NotNull
    private Ingredient.Unit unit;

    public ManualIngredientDto() {}

    public ManualIngredientDto(ManualIngredient ingredient) {
        super(ingredient);
        BeanUtils.copyProperties(ingredient, this);
    }

    @Override
    public String getType() {
        return "manual";
    }

    public Ingredient.Unit getUnit() {
        return unit;
    }

    public void setUnit(Ingredient.Unit unit) {
        this.unit = unit;
    }
}
