package net.alex9849.cocktailpi.payload.dto.cocktail;

import lombok.*;
import net.alex9849.cocktailpi.model.FeasibilityReport;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.AddableIngredientDto;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.IngredientDto;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.IngredientGroupDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeasibilityReportDto {
    private interface RequiredIngredients { List<RequiredIngredientDto.Response.Detailed> getRequiredIngredients(); }
    private interface IngredientGroupReplacements { List<IngredientGroupReplacementDto.Response.Detailed> getIngredientGroupReplacements(); }
    private interface IsFeasible { boolean isFeasible(); }
    private interface TotalAmountInMl { int getTotalAmountInMl(); }
    private interface FailNoGlass { boolean isFailNoGlass(); }
    private interface IsAllIngredientGroupsReplaced { boolean isAllIngredientGroupsReplaced(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements IngredientGroupReplacements, IsFeasible,
                IsAllIngredientGroupsReplaced, RequiredIngredients, FailNoGlass, TotalAmountInMl {

            List<IngredientGroupReplacementDto.Response.Detailed> ingredientGroupReplacements;
            List<RequiredIngredientDto.Response.Detailed> requiredIngredients;
            boolean allIngredientGroupsReplaced;
            boolean isFeasible;
            boolean failNoGlass;
            int totalAmountInMl;

            public Detailed(FeasibilityReport report) {
                this.ingredientGroupReplacements = report.getIngredientGroupReplacements()
                        .stream().map(IngredientGroupReplacementDto.Response.Detailed::new)
                        .collect(Collectors.toList());
                this.requiredIngredients = report.getRequiredIngredients().stream()
                        .map(RequiredIngredientDto.Response.Detailed::new)
                        .collect(Collectors.toList());
                this.requiredIngredients.sort(Comparator.comparing(x -> x.getIngredient().getName()));
                this.allIngredientGroupsReplaced = report.isAllIngredientGroupsReplaced();
                this.failNoGlass = report.isFailNoGlass();
                this.isFeasible = report.isFeasible();
                this.totalAmountInMl = report.getTotalAmountInMl();
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RequiredIngredientDto {
        private interface Ingredient { IngredientDto.Response.Detailed getIngredient(); }
        private interface AmountRequired { int getAmountRequired(); }
        private interface AmountMissing { int getAmountMissing(); }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Response {
            @Getter @Setter @EqualsAndHashCode
            public static class Detailed implements Ingredient, AmountRequired, AmountMissing {
                IngredientDto.Response.Detailed ingredient;
                int amountRequired;
                int amountMissing;

                public Detailed(FeasibilityReport.RequiredIngredient value) {
                    this.ingredient = IngredientDto.Response.Detailed.toDto(value.getIngredient());
                    this.amountMissing = value.getAmountMissing();
                    this.amountRequired = value.getAmountRequired();
                }
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IngredientGroupReplacementDto {
        private interface IngredientGroupToReplace { IngredientGroupDto.Response.Reduced getIngredientGroup(); }
        private interface IngredientGroupIdToReplace { long getIngredientGroupId(); }
        private interface SelectedReplacement { AddableIngredientDto.Response.Detailed getSelectedReplacement(); }
        private interface SelectedReplacementId { Long getReplacementId(); }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Request {
            @Getter @Setter @EqualsAndHashCode
            public static class Create implements IngredientGroupIdToReplace, SelectedReplacementId {
                long ingredientGroupId;
                Long replacementId;
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
