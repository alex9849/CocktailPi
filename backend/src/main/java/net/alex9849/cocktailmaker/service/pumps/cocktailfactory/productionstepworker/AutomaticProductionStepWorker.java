package net.alex9849.cocktailmaker.service.pumps.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailmaker.service.pumps.cocktailfactory.PumpTimingStepCalculator;

import java.util.*;
import java.util.stream.Collectors;

public class AutomaticProductionStepWorker extends AbstractPumpingProductionStepWorker {
    /**
     *
     * @param pumps pumps is an output parameter! The attribute fillingLevelInMl will be decreased according to the recipe
     */
    public AutomaticProductionStepWorker(Set<Pump> pumps, List<ProductionStepIngredient> productionStepInstructions, int minimalPumpTime, int minimalBreakTime) {
        Map<Long, List<Pump>> pumpsByIngredientId = pumps.stream()
                .filter(x -> x.getCurrentIngredient() != null)
                .collect(Collectors
                .groupingBy(x -> x.getCurrentIngredient().getId()));

        Set<PumpStepIngredient> pumpStepIngredients = new HashSet<>();
        for (ProductionStepIngredient instruction : productionStepInstructions) {
            if (!(instruction.getIngredient() instanceof AutomatedIngredient currAutoIngredient)) {
                throw new IllegalArgumentException("Can't automatically fulfill productionstep. One or more given ingredients can only be added manually!");
            }

            List<Pump> pumpsWithIngredient = pumpsByIngredientId.get(currAutoIngredient.getId());
            if (pumpsWithIngredient == null) {
                throw new IllegalArgumentException("Can't automatically fulfill productionstep. One or more required ingredients are not assigned to pumps!");
            }


            PumpStepIngredient pumpStepIngredient = new PumpStepIngredient(instruction.getIngredient(), instruction.getAmount(), pumpsWithIngredient);
            pumpStepIngredients.add(pumpStepIngredient);
        }
        PumpTimingStepCalculator pumpTimingStepCalculator = new PumpTimingStepCalculator(pumpStepIngredients,
                minimalPumpTime, minimalBreakTime);

        this.setDcPumpPhases(pumpTimingStepCalculator.getPumpPhases());
        this.setDriversToComplete(pumpTimingStepCalculator.getSteppersToComplete());
    }
}
