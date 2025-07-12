package net.alex9849.cocktailpi.service.pumps.cocktailfactory.productionstepworker;

import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.CocktailFactory;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.PumpTimingStepCalculator;

import java.util.*;
import java.util.stream.Collectors;

public class AutomaticProductionStepWorker extends AbstractPumpingProductionStepWorker {

    public AutomaticProductionStepWorker(CocktailFactory cocktailFactory, PumpTimingStepCalculator calculator) {
        super(cocktailFactory);
        this.setDcPumpPhases(calculator.getPumpPhases());
        this.setSteppersToComplete(calculator.getSteppersToComplete());
        this.setValvesToRequestedGrams(calculator.getValvesToRequestedGrams());
    }

    public static List<PumpTimingStepCalculator> splitStepByPowerLimit(Set<Pump> pumps, List<ProductionStepIngredient> productionStepInstructions,
                                                                    int minimalPumpTime, int minimalBreakTime, Map<Pump, Integer> remainingFillingLevelByPump,
                                                                    Integer powerLimit) {
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

        if(powerLimit == null) {
            return List.of(new PumpTimingStepCalculator(pumpStepIngredients, minimalPumpTime, minimalBreakTime, remainingFillingLevelByPump));
        }
        PumpTimingStepCalculator calculator = new PumpTimingStepCalculator(pumpStepIngredients, minimalPumpTime, minimalBreakTime, new HashMap<>(remainingFillingLevelByPump));

        int remainingPowerLimit = powerLimit;
        List<Set<PumpStepIngredient>> newProductionSteps = new ArrayList<>();
        Set<PumpStepIngredient> currentPumpStepIngredients = new HashSet<>();

        for (PumpStepIngredient stepIngredient : pumpStepIngredients) {
            Set<Pump> stepPumps = calculator.getUsedPumps();
            stepPumps.retainAll(stepIngredient.getApplicablePumps());

            List<Pump> stepIngredientPumps = new ArrayList<>();
            int remainingAmount = stepIngredient.getAmount();
            for (Pump pump : stepPumps) {
                if(pump.getPowerConsumption() > remainingPowerLimit) {
                    int pumpAmount = 0;
                    for (Pump stepPump : stepIngredientPumps) {
                        pumpAmount += remainingFillingLevelByPump.getOrDefault(stepPump, stepPump.getFillingLevelInMl());
                    }
                    pumpAmount = Math.min(pumpAmount, remainingAmount);
                    if (pumpAmount > 0) {
                        currentPumpStepIngredients.add(new PumpStepIngredient(stepIngredient.getIngredient(), pumpAmount, stepIngredientPumps));
                    }
                    if(!currentPumpStepIngredients.isEmpty()) {
                        newProductionSteps.add(currentPumpStepIngredients);
                    }
                    currentPumpStepIngredients = new HashSet<>();
                    stepIngredientPumps = new ArrayList<>();
                    remainingPowerLimit = powerLimit;

                }
                remainingPowerLimit -= pump.getPowerConsumption();
                stepIngredientPumps.add(pump);
            }
            currentPumpStepIngredients.add(new PumpStepIngredient(stepIngredient.getIngredient(), remainingAmount, stepIngredientPumps));
        }
        if(!currentPumpStepIngredients.isEmpty()) {
            newProductionSteps.add(currentPumpStepIngredients);
        }

        List<PumpTimingStepCalculator> calculators = new ArrayList<>();
        for(Set<PumpStepIngredient> psi : newProductionSteps) {
            calculator = new PumpTimingStepCalculator(psi, minimalPumpTime, minimalBreakTime, remainingFillingLevelByPump);
            calculators.add(calculator);
            remainingFillingLevelByPump = calculator.getRemainingFillingLevelByPump();
        }
        return calculators;
    }
}
