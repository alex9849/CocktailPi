package net.alex9849.cocktailmaker.payload.dto.cocktail;

import lombok.*;
import net.alex9849.cocktailmaker.model.FeasibilityReport;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.IngredientGroup;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.AddableIngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.IngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.IngredientGroupDto;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeasibilityReportDto {
    private interface InsufficientIngredients { List<InsufficientIngredientDto.Response.Detailed> getInsufficientIngredients(); }
    private interface IngredientGroupReplacements { List<List<IngredientGroupReplacementDto.Response.Detailed>> getIngredientGroupReplacements(); }
    private interface IsFeasible { boolean isFeasible(); }
    private interface IsAllIngredientGroupsReplaced { boolean isAllIngredientGroupsReplaced(); }
    private interface IngredientsToAddManually { Set<IngredientDto.Response.Reduced> getIngredientsToAddManually(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements InsufficientIngredients, IngredientGroupReplacements,
                IngredientsToAddManually, IsFeasible, IsAllIngredientGroupsReplaced {

            List<InsufficientIngredientDto.Response.Detailed> insufficientIngredients;
            List<List<IngredientGroupReplacementDto.Response.Detailed>> ingredientGroupReplacements;
            Set<IngredientDto.Response.Reduced> ingredientsToAddManually;
            boolean allIngredientGroupsReplaced;
            boolean isFeasible;

            public Detailed(FeasibilityReport report) {
                this.insufficientIngredients = report.getInsufficientIngredients().stream()
                        .map(InsufficientIngredientDto.Response.Detailed::new)
                        .collect(Collectors.toList());
                this.ingredientGroupReplacements = new ArrayList<>();
                for (List<FeasibilityReport.IngredientGroupReplacement> pStepReplacements : report.getIngredientGroupReplacements()) {
                    List<IngredientGroupReplacementDto.Response.Detailed> pStepReplacementsDtos = pStepReplacements.stream().map(IngredientGroupReplacementDto.Response.Detailed::new).collect(Collectors.toList());
                    this.ingredientGroupReplacements.add(pStepReplacementsDtos);
                }
                this.ingredientsToAddManually = report.getIngredientsToAddManually()
                        .stream().map(IngredientDto.Response.Reduced::toDto)
                        .collect(Collectors.toSet());
                this.allIngredientGroupsReplaced = report.isAllIngredientGroupsReplaced();
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

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IngredientGroupReplacementDto {
        private interface IngredientGroupToReplace { IngredientGroupDto.Response.Reduced getIngredientGroup(); }
        private interface IngredientGroupIdToReplace { long getIngredientGroupId(); }
        private interface SelectedReplacement { AddableIngredientDto.Response.Detailed getSelectedReplacement(); }
        private interface SelectedReplacementId { long getReplacementId(); }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Request {
            @Getter @Setter @EqualsAndHashCode
            public static class Create implements IngredientGroupIdToReplace, SelectedReplacementId {
                long ingredientGroupId;
                long replacementId;
            }
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Response {
            @Getter @Setter @EqualsAndHashCode
            public static class Detailed implements IngredientGroupToReplace, SelectedReplacement {
                IngredientGroupDto.Response.Reduced ingredientGroup;
                AddableIngredientDto.Response.Detailed selectedReplacement;

                public Detailed(FeasibilityReport.IngredientGroupReplacement value) {
                    this.ingredientGroup = new IngredientGroupDto.Response.Reduced(value.getIngredientGroup());
                    this.selectedReplacement = AddableIngredientDto.Response.Detailed.toDto(value.getSelectedReplacement());
                }
            }
        }
    }

}
