package net.alex9849.cocktailmaker.payload.dto.recipe.productionstep;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import net.alex9849.cocktailmaker.model.recipe.AddIngredientsProductionStep;
import net.alex9849.cocktailmaker.model.recipe.ProductionStep;
import net.alex9849.cocktailmaker.model.recipe.WrittenInstructionProductionStep;
import org.springframework.beans.BeanUtils;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductionStepDto {
    private interface Type { String getType(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = WrittenInstructionProductionStepDto.class, name = "writtenInstruction"),
                @JsonSubTypes.Type(value = AddIngredientsProductionStepDto.class, name = "addIngredients")
        })
        public abstract static class Create implements Type {

        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public abstract static class Detailed implements Type {

            protected Detailed(ProductionStep productionStep) {
                BeanUtils.copyProperties(productionStep, this);
            }

            public static Detailed toDto(ProductionStep productionStep) {
                if (productionStep == null) {
                    return null;
                }
                if (productionStep instanceof WrittenInstructionProductionStep) {
                    return new WrittenInstructionProductionStepDto.Response.Detailed((WrittenInstructionProductionStep) productionStep);
                }
                if (productionStep instanceof AddIngredientsProductionStep) {
                    return new AddIngredientsProductionStepDto.Response.Detailed((AddIngredientsProductionStep) productionStep);
                }
                throw new IllegalStateException("ProductionStepType is not supported: " + productionStep.getClass().getName());
            }
        }
    }
}