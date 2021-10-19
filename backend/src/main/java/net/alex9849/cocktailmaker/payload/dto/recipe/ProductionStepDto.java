package net.alex9849.cocktailmaker.payload.dto.recipe;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.alex9849.cocktailmaker.model.recipe.AddIngredientsProductionStep;
import net.alex9849.cocktailmaker.model.recipe.ProductionStep;
import net.alex9849.cocktailmaker.model.recipe.WrittenInstructionProductionStep;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = WrittenInstructionProductionStepDto.class, name = "writtenInstruction"),
        @JsonSubTypes.Type(value = AddIngredientsProductionStepDto.class, name = "addIngredients")
})
public interface ProductionStepDto {
    String getType();

    static ProductionStepDto toDto(ProductionStep productionStep) {
        if(productionStep instanceof WrittenInstructionProductionStep) {
            return new WrittenInstructionProductionStepDto((WrittenInstructionProductionStep) productionStep);
        }
        if(productionStep instanceof AddIngredientsProductionStep) {
            return new AddIngredientsProductionStepDto((AddIngredientsProductionStep) productionStep);
        }
        throw new IllegalStateException("ProductionStepType is not supported yet!");
    }
}
