package net.alex9849.cocktailmaker.payload.dto.recipe.ingredient;

import lombok.*;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ManualIngredient;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ManualIngredientDto {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        public static class Create extends AddableIngredientDto.Request.Create {
            Ingredient.Unit unit;

            @Override
            public String getType() {
                return "manual";
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public static class Detailed extends AddableIngredientDto.Response.Detailed {
            Ingredient.Unit unit;

            protected Detailed(ManualIngredient ingredient) {
                super(ingredient);
            }

            @Override
            public String getType() {
                return "manual";
            }

        }

        @Getter @Setter @EqualsAndHashCode
        public static class Reduced extends AddableIngredientDto.Response.Reduced {

            protected Reduced(ManualIngredient ingredient) {
                super(ingredient);
            }

            @Override
            public String getType() {
                return "manual";
            }
        }
    }
}