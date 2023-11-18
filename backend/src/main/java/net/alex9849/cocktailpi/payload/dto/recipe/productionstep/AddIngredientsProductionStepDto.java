package net.alex9849.cocktailpi.payload.dto.recipe.productionstep;

import lombok.*;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddIngredientsProductionStepDto {
    private interface StepIngredientsCreated { List<ProductionStepIngredientDto.Request.Create> getStepIngredients(); }
    private interface StepIngredientsDetailed { List<ProductionStepIngredientDto.Response.Detailed> getStepIngredients(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Create extends ProductionStepDto.Request.Create {
            List<ProductionStepIngredientDto.Request.Create> stepIngredients;

            public Create() {}

            public Create(Response.Detailed detailed) {
                super(detailed);
                this.stepIngredients = detailed.getStepIngredients().stream()
                        .map(ProductionStepIngredientDto.Request.Create::new)
                        .collect(Collectors.toList());
            }

            @Override
            public String getType() {
                return "addIngredients";
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends ProductionStepDto.Response.Detailed {
            List<ProductionStepIngredientDto.Response.Detailed> stepIngredients;

            public Detailed() {}

            public Detailed(AddIngredientsProductionStep productionStep) {
                super(productionStep);
                stepIngredients = productionStep.getStepIngredients().stream()
                        .map(ProductionStepIngredientDto.Response.Detailed::new).collect(Collectors.toList());
            }

            @Override
            public String getType() {
                return "addIngredients";
            }
        }
    }
}
