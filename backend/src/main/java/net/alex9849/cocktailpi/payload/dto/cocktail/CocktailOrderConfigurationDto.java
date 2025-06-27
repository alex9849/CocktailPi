package net.alex9849.cocktailpi.payload.dto.cocktail;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CocktailOrderConfigurationDto {
    public interface AmountToProduce { @Min(10) @Max(5000) Integer getAmountOrderedInMl(); }
    public interface IngredientGroupReplacements { @NotNull() List<FeasibilityReportDto.IngredientGroupReplacementDto.Request.Create> getIngredientGroupReplacements(); }
    public interface Customisations { @NotNull() CustomisationsDto.Request.Create getCustomisations(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        public static class Create implements AmountToProduce, IngredientGroupReplacements, Customisations {
            Integer amountOrderedInMl;
            List<FeasibilityReportDto.IngredientGroupReplacementDto.Request.Create> ingredientGroupReplacements;
            CustomisationsDto.Request.Create customisations;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CustomisationsDto {
        public interface Boost { @Min(0) @Max(200) int getBoost(); }
        public interface AdditionalIngredients { @NotNull() List<AdditionalIngredientDto.Request.Create> getAdditionalIngredients(); }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Request {
            @Getter @Setter @EqualsAndHashCode
            public static class Create implements Boost, AdditionalIngredients {
                int boost;
                List<AdditionalIngredientDto.Request.Create> additionalIngredients;
            }
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class AdditionalIngredientDto {
            private interface IngredientId { @NotNull long getIngredientId(); }
            private interface Amount { @Min(1) int getAmount(); }

            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static class Request {
                @Getter @Setter @EqualsAndHashCode
                public static class Create implements IngredientId, Amount {
                    long ingredientId;
                    int amount;
                }
            }
        }
    }

}
