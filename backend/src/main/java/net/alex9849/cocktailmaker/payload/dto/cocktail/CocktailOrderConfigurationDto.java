package net.alex9849.cocktailmaker.payload.dto.cocktail;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CocktailOrderConfigurationDto {
    public interface AmountToProduce { @Min(50) @Max(1000) Integer getAmountOrderedInMl(); }
    public interface ProductionStepReplacements { @NotNull() List<List<FeasibilityReportDto.IngredientGroupReplacementDto.Request.Create>> getProductionStepReplacements(); }
    public interface Customisations { @NotNull() CustomisationsDto.Request.Create getCustomisations(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        public static class Create implements AmountToProduce, ProductionStepReplacements, Customisations {
            Integer amountOrderedInMl;
            List<List<FeasibilityReportDto.IngredientGroupReplacementDto.Request.Create>> productionStepReplacements;
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
