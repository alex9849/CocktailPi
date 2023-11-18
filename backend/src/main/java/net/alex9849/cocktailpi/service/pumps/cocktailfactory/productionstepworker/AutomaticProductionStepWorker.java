package net.alex9849.cocktailpi.service.pumps.cocktailfactory.productionstepworker;

import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.CocktailFactory;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.PumpTimingStepCalculator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AutomaticProductionStepWorker extends AbstractPumpingProductionStepWorker {
    /**
     *
     * @param pumps pumps is an output parameter! The attribute fillingLevelInMl will be decreased according to the recipe
     */
    public AutomaticProductionStepWorker(CocktailFactory cocktailFactory, Set<Pump> pumps,
                                         List<ProductionStepIngredient> productionStepInstructions, int minimalPumpTime, int minimalBreakTime) {
        super(cocktailFactory);
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
        this.setSteppersToComplete(pumpTimingStepCalculator.getSteppersToComplete());
    }
}
