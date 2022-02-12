package net.alex9849.cocktailmaker.payload.dto.cocktail;

import lombok.*;
import net.alex9849.cocktailmaker.model.FeasibilityReport;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.IngredientDto;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeasibilityReportDto {
    private interface InsufficientIngredients { List<InsufficientIngredientDto.Response.Detailed> getInsufficientIngredients(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public static class Detailed {
            List<InsufficientIngredientDto.Response.Detailed> insufficientIngredients;

            public Detailed(FeasibilityReport report) {
                this.insufficientIngredients = report.getInsufficientIngredients().stream()
                        .map(InsufficientIngredientDto.Response.Detailed::new)
                        .collect(Collectors.toList());
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class InsufficientIngredientDto {
        private interface Ingredient { IngredientDto.Response.Reduced getIngredient(); }
        private interface AmountNeeded { int getAmountNeeded(); }
        private interface AmountRemaining { int getAmountRemaining(); }


        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Response {
            @Getter @Setter @EqualsAndHashCode
            public static class Detailed implements Ingredient, AmountNeeded, AmountRemaining {
                IngredientDto.Response.Reduced ingredient;
                int amountNeeded;
                int amountRemaining;

                public Detailed(FeasibilityReport.InsufficientIngredient value) {
                    this.ingredient = IngredientDto.Response.Reduced.toDto(value.getIngredient());
                    this.amountNeeded = value.getAmountNeeded();
                    this.amountRemaining = value.getAmountRemaining();
                }
            }
        }
    }
}
