package net.alex9849.cocktailpi.payload.dto.recipe.ingredient;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.alex9849.cocktailpi.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AutomatedIngredientDto {
    private interface PumpTimeMultiplier { @NotNull @Min(1) double getPumpTimeMultiplier(); }
    private interface BottleSize { @NotNull @Min(0) Integer getBottleSize();}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Create extends AddableIngredientDto.Request.Create implements BottleSize, PumpTimeMultiplier {
            double pumpTimeMultiplier;
            Integer bottleSize;

            public Create() {}

            public Create(Response.Detailed detailed) {
                super(detailed);
            }

            @Override
            public String getType() {
                return "automated";
            }

        }

    }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends AddableIngredientDto.Response.Detailed implements PumpTimeMultiplier, BottleSize {
            double pumpTimeMultiplier;
            Integer bottleSize;
            boolean onPump;

            public Detailed() {}

            public Detailed(AutomatedIngredient ingredient) {
                super(ingredient);
            }

            @Override
            public String getType() {
                return "automated";
            }

            @Override
            public Ingredient.Unit getUnit() {
                return Ingredient.Unit.MILLILITER;
            }
        }

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Reduced extends AddableIngredientDto.Response.Reduced {
            boolean onPump;

            public Reduced(AutomatedIngredient ingredient) {
                super(ingredient);
            }

            @Override
            public String getType() {
                return "automated";
            }
        }
    }
}
