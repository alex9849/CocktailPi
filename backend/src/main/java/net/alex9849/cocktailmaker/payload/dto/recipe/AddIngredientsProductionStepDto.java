package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.AddIngredientsProductionStep;

import java.util.List;
import java.util.stream.Collectors;

public class AddIngredientsProductionStepDto implements ProductionStepDto {
    private List<ProductionStepIngredientDto> stepIngredients;

    public AddIngredientsProductionStepDto(AddIngredientsProductionStep addIngredientsProductionStep) {
        stepIngredients = addIngredientsProductionStep.getStepIngredients().stream()
                .map(ProductionStepIngredientDto::new).collect(Collectors.toList());
    }

    public AddIngredientsProductionStepDto() {}

    public List<ProductionStepIngredientDto> getStepIngredients() {
        return stepIngredients;
    }

    public void setStepIngredients(List<ProductionStepIngredientDto> stepIngredients) {
        this.stepIngredients = stepIngredients;
    }

    @Override
    public String getType() {
        return "addIngredients";
    }
}
