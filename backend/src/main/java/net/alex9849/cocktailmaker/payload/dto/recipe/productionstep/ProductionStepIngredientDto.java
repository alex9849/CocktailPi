package net.alex9849.cocktailmaker.payload.dto.recipe.productionstep;

import lombok.*;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.IngredientDto;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductionStepIngredientDto {
    private interface IngredientId { @NotNull long getIngredientId(); }
    private interface Ingredient { @NotNull IngredientDto.Response.Detailed getIngredient(); }
    private interface Amount { @Min(1) int getAmount(); }
    private interface Scale { boolean isScale(); }
    private interface Boostable { boolean isBoostable(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        public static class Create implements IngredientId, Amount, Scale, Boostable {
            long ingredientId;
            int amount;
            boolean scale;
            boolean boostable;

            public Create() {}

            public Create(Response.Detailed detailed) {
                BeanUtils.copyProperties(detailed, this);
                this.ingredientId = detailed.getIngredient().getId();
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements Ingredient, Amount, Scale, Boostable {
            IngredientDto.Response.Detailed ingredient;
            int amount;
            boolean scale;
            boolean boostable;

            public Detailed() {}

            public Detailed(ProductionStepIngredient stepIngredient) {
                BeanUtils.copyProperties(stepIngredient, this);
                this.ingredient = IngredientDto.Response.Detailed.toDto(stepIngredient.getIngredient());
            }
        }
    }
}
