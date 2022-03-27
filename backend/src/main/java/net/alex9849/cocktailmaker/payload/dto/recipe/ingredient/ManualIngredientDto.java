package net.alex9849.cocktailmaker.payload.dto.recipe.ingredient;

import lombok.*;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.ManualIngredient;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ManualIngredientDto {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Create extends AddableIngredientDto.Request.Create implements IngredientDto.Unit {
            Ingredient.Unit unit;

            @Override
            public String getType() {
                return "manual";
            }

            @Override
            public Long getParentGroupId() {
                if(this.unit != Ingredient.Unit.MILLILITER) {
                    return null;
                }
                return super.getParentGroupId();
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends AddableIngredientDto.Response.Detailed {
            Ingredient.Unit unit;

            protected Detailed(ManualIngredient ingredient) {
                super(ingredient);
            }

            @Override
            public String getType() {
                return "manual";
            }

            @Override
            public boolean isOnPump() {
                return false;
            }
        }

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Reduced extends AddableIngredientDto.Response.Reduced {

            protected Reduced(ManualIngredient ingredient) {
                super(ingredient);
            }

            @Override
            public String getType() {
                return "manual";
            }

            @Override
            public boolean isOnPump() {
                return false;
            }
        }
    }
}
