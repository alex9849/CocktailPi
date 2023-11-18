package net.alex9849.cocktailpi.payload.dto.recipe.productionstep;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.WrittenInstructionProductionStep;
import org.springframework.beans.BeanUtils;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductionStepDto {
    private interface Type { String getType(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = WrittenInstructionProductionStepDto.Request.Create.class, name = "writtenInstruction"),
                @JsonSubTypes.Type(value = AddIngredientsProductionStepDto.Request.Create.class, name = "addIngredients")
        })
        public abstract static class Create implements Type {

            protected Create() {}

            protected Create(Response.Detailed detailed) {
                BeanUtils.copyProperties(detailed, this);
            }

            public static Create fromDetailedDto(Response.Detailed detailed) {
                if(detailed == null) {
                    return null;
                }
                if (detailed instanceof WrittenInstructionProductionStepDto.Response.Detailed) {
                    return new WrittenInstructionProductionStepDto.Request.Create((WrittenInstructionProductionStepDto.Response.Detailed) detailed);
                }
                if (detailed instanceof AddIngredientsProductionStepDto.Response.Detailed) {
                    return new AddIngredientsProductionStepDto.Request.Create((AddIngredientsProductionStepDto.Response.Detailed) detailed);
                }
                throw new IllegalStateException("ProductionStepType is not supported: " + detailed.getClass().getName());
            }

        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = WrittenInstructionProductionStepDto.Response.Detailed.class, name = "writtenInstruction"),
                @JsonSubTypes.Type(value = AddIngredientsProductionStepDto.Response.Detailed.class, name = "addIngredients")
        })
        public abstract static class Detailed implements Type {

            protected Detailed() {}

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
