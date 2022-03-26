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

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        public static class Create implements AmountToProduce, ProductionStepReplacements {
            Integer amountOrderedInMl;
            List<List<FeasibilityReportDto.IngredientGroupReplacementDto.Request.Create>> productionStepReplacements;
        }
    }
}
