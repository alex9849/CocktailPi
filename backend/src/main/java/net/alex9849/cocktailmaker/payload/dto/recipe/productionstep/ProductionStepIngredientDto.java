package net.alex9849.cocktailmaker.payload.dto.recipe.productionstep;

import lombok.*;
import net.alex9849.cocktailmaker.model.recipe.ProductionStepIngredient;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.IngredientDto;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductionStepIngredientDto {
    private interface Ingredient { @NotNull IngredientDto.Duplex.Detailed getIngredient(); }
    private interface Amount { @Min(1) int getAmount(); }
    private interface Scale { boolean isScale(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        public static class Create implements Ingredient, Amount, Scale {
            IngredientDto.Duplex.Detailed ingredient;
            int amount;
            boolean scale;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements Ingredient, Amount, Scale {
            IngredientDto.Duplex.Detailed ingredient;
            int amount;
            boolean scale;

            public Detailed(ProductionStepIngredient stepIngredient) {
                BeanUtils.copyProperties(stepIngredient, this);
                this.ingredient = IngredientDto.Duplex.Detailed.toDto(stepIngredient.getIngredient());
            }
        }
    }
}
