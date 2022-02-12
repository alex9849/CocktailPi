package net.alex9849.cocktailmaker.payload.dto;

import net.alex9849.cocktailmaker.model.FeasibilityReport;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.IngredientDto;

import java.util.List;
import java.util.stream.Collectors;

public class FeasibilityReportDto {
    private List<InsufficientIngredientDto> insufficientIngredients;

    public FeasibilityReportDto(FeasibilityReport report) {
        this.insufficientIngredients = report.getInsufficientIngredients().stream()
                .map(InsufficientIngredientDto::new).collect(Collectors.toList());
    }

    public List<InsufficientIngredientDto> getInsufficientIngredients() {
        return insufficientIngredients;
    }

    public void setInsufficientIngredients(List<InsufficientIngredientDto> insufficientIngredients) {
        this.insufficientIngredients = insufficientIngredients;
    }

    public static class InsufficientIngredientDto {
        private IngredientDto ingredient;
        private int amountNeeded;
        private int amountRemaining;

        public InsufficientIngredientDto(FeasibilityReport.InsufficientIngredient value) {
            this.ingredient = IngredientDto.toDto(value.getIngredient());
            this.amountNeeded = value.getAmountNeeded();
            this.amountRemaining = value.getAmountRemaining();
        }

        public IngredientDto getIngredient() {
            return ingredient;
        }

        public void setIngredient(IngredientDto ingredient) {
            this.ingredient = ingredient;
        }

        public int getAmountNeeded() {
            return amountNeeded;
        }

        public void setAmountNeeded(int amountNeeded) {
            this.amountNeeded = amountNeeded;
        }

        public int getAmountRemaining() {
            return amountRemaining;
        }

        public void setAmountRemaining(int amountRemaining) {
            this.amountRemaining = amountRemaining;
        }
    }
}
