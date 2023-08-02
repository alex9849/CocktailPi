package net.alex9849.cocktailmaker.service.pumps.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailmaker.service.pumps.cocktailfactory.PumpTimingStepCalculator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AutomaticProductionStepWorker extends AbstractPumpingProductionStepWorker {
    /**
     *
     * @param pumps pumps is an output parameter! The attribute fillingLevelInMl will be decreased according to the recipe
     */
    public AutomaticProductionStepWorker(Set<DcPump> pumps, List<ProductionStepIngredient> productionStepInstructions, int minimalPumpTime, int minimalBreakTime) {
        Map<Long, List<DcPump>> pumpsByIngredientId = pumps.stream()
                .filter(x -> x.getCurrentIngredient() != null)
                .collect(Collectors
                .groupingBy(x -> x.getCurrentIngredient().getId()));

        Map<ProductionStepIngredient, List<DcPump>> matchingPumpByProductionStepIngredient = new HashMap<>();
        for (ProductionStepIngredient instruction : productionStepInstructions) {
            if (!(instruction.getIngredient() instanceof AutomatedIngredient)) {
                throw new IllegalArgumentException("Can't automatically fulfill productionstep. One or more given ingredients can only be added manually!");
            }
            AutomatedIngredient currAutoIngredient = (AutomatedIngredient) instruction.getIngredient();
            List<DcPump> pumpsWithIngredient = pumpsByIngredientId.get(currAutoIngredient.getId());
            if (pumpsWithIngredient == null) {
                throw new IllegalArgumentException("Can't automatically fulfill productionstep. One or more required ingredients are not assigned to pumps!");
            }
            matchingPumpByProductionStepIngredient.put(instruction, pumpsWithIngredient);
        }
        PumpTimingStepCalculator pumpTimingStepCalculator = new PumpTimingStepCalculator(matchingPumpByProductionStepIngredient,
                minimalPumpTime, minimalBreakTime);

        this.setDcPumpPhases(pumpTimingStepCalculator.getPumpPhases());
    }
}
