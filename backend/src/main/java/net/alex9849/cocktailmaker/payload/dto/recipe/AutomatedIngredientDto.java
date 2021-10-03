package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class AutomatedIngredientDto extends IngredientDto {

    @NotNull
    @Min(1)
    private double pumpTimeMultiplier;

    public AutomatedIngredientDto() {}

    @Override
    public String getType() {
        return "automated";
    }

    public AutomatedIngredientDto(AutomatedIngredient ingredient) {
        super(ingredient);
        BeanUtils.copyProperties(ingredient, this);
    }

    public boolean isScaleToVolume() {
        return true;
    }

    public double getPumpTimeMultiplier() {
        return pumpTimeMultiplier;
    }

    public void setPumpTimeMultiplier(double pumpTimeMultiplier) {
        this.pumpTimeMultiplier = pumpTimeMultiplier;
    }
}
