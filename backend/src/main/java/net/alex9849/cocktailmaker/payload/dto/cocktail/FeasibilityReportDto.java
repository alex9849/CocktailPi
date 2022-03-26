package net.alex9849.cocktailmaker.payload.dto.cocktail;

import lombok.*;
import net.alex9849.cocktailmaker.model.FeasibilityReport;
import net.alex9849.cocktailmaker.model.recipe.ingredient.IngredientGroup;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.IngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.IngredientGroupDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeasibilityReportDto {
    private interface InsufficientIngredients { List<InsufficientIngredientDto.Response.Detailed> getInsufficientIngredients(); }
    private interface MissingIngredientGroupReplacements { HashMap<Long, List<IngredientGroupDto.Response.Reduced>> getMissingIngredientGroupReplacements(); }
    private interface IsFeasible { boolean isFeasible(); }
    private interface IngredientsToAddManually { Set<IngredientDto.Response.Reduced> getIngredientsToAddManually(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements InsufficientIngredients, MissingIngredientGroupReplacements,
                IngredientsToAddManually, IsFeasible {

            List<InsufficientIngredientDto.Response.Detailed> insufficientIngredients;
            HashMap<Long, List<IngredientGroupDto.Response.Reduced>> missingIngredientGroupReplacements;
            Set<IngredientDto.Response.Reduced> ingredientsToAddManually;
            boolean isFeasible;

            public Detailed(FeasibilityReport report) {
                this.insufficientIngredients = report.getInsufficientIngredients().stream()
                        .map(InsufficientIngredientDto.Response.Detailed::new)
                        .collect(Collectors.toList());
                this.missingIngredientGroupReplacements = new HashMap<>();
                for (Map.Entry<Long, List<IngredientGroup>> entry : report.getMissingIngredientGroupReplacements().entrySet()) {
                    List<IngredientGroupDto.Response.Reduced> dtoIngredientGroups = entry.getValue().stream().map(IngredientGroupDto.Response.Reduced::new).collect(Collectors.toList());
                    this.missingIngredientGroupReplacements.put(entry.getKey(), dtoIngredientGroups);
                }
                this.ingredientsToAddManually = report.getIngredientsToAddManually()
                        .stream().map(IngredientDto.Response.Reduced::toDto)
                        .collect(Collectors.toSet());
                this.isFeasible = report.isFeasible();
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
