package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ManualIngredient;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;

public class ManualIngredientDto extends IngredientDto {

    private Ingredient.Unit unit;

    @NotNull
    private boolean addToVolume;

    public ManualIngredientDto() {}

    public ManualIngredientDto(ManualIngredient ingredient) {
        super(ingredient);
        BeanUtils.copyProperties(ingredient, this);
    }

    public Ingredient.Unit getUnit() {
        return unit;
    }

    public void setUnit(Ingredient.Unit unit) {
        this.unit = unit;
    }

    public boolean isAddToVolume() {
        return addToVolume;
    }

    public void setAddToVolume(boolean addToVolume) {
        this.addToVolume = addToVolume;
    }
}
