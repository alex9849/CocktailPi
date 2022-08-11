package net.alex9849.cocktailmaker.service.cocktailfactory;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStepIngredient;

import java.util.*;

public class PumpTimingStepCalculator {
    private final Integer longestPumpRunTime;
    private final Pump longestIngredientPump;
    private final Map<Pump, Integer> otherPumpTimings;
    private final Set<Pump> updatedPumps;
    private final int minimalPumpTime;
    private final int minimalBreakTime;

    public PumpTimingStepCalculator(Map<ProductionStepIngredient, List<Pump>> matchingPumpByProductionStepIngredient, int minimalPumpTime, int minimalBreakTime) {
        if(minimalPumpTime <= 0) {
            throw new IllegalArgumentException("minimalPumpTime needs to be at least 1!");
        }
        this.minimalPumpTime = minimalPumpTime;
        if(minimalBreakTime <= 0) {
            throw new IllegalArgumentException("minimalBreakTime needs to be at least 1!");
        }
        this.minimalBreakTime = minimalBreakTime;
        if(matchingPumpByProductionStepIngredient.size() == 0) {
            throw new IllegalArgumentException("matchingPumpByProductionStepIngredient must be non empty!");
        }
        this.updatedPumps = new HashSet<>();
        //Prioritize pumps with a low filling level
        matchingPumpByProductionStepIngredient.values().forEach(x -> x.sort(Comparator.comparingInt(Pump::getFillingLevelInMl)));

        Map.Entry<Pump, Integer> longestRuntime = null;
        Map<Pump, Integer> timeToRunPerPump = new HashMap<>();

        for(Map.Entry<ProductionStepIngredient, List<Pump>> currentPumpsRuntime : matchingPumpByProductionStepIngredient.entrySet()) {
            int remainingAmountToFillInMl = currentPumpsRuntime.getKey().getAmount();
            for(Pump pump : currentPumpsRuntime.getValue()) {
                if(pump.getFillingLevelInMl() == 0) {
                    continue;
                }
                if(remainingAmountToFillInMl <= 0) {
                    break;
                }
                int amountToFillForPumpInMl;
                if(pump.getFillingLevelInMl() > remainingAmountToFillInMl) {
                    amountToFillForPumpInMl = remainingAmountToFillInMl;
                    pump.setFillingLevelInMl(pump.getFillingLevelInMl() - amountToFillForPumpInMl);
                } else {
                    amountToFillForPumpInMl = pump.getFillingLevelInMl();
                    pump.setFillingLevelInMl(0);
                }
                int timeToRun = pump.getConvertMlToRuntime(amountToFillForPumpInMl);
                timeToRunPerPump.put(pump, timeToRun);
                this.updatedPumps.add(pump);
                remainingAmountToFillInMl -= amountToFillForPumpInMl;
            }
            if(remainingAmountToFillInMl > 0) {
                throw new IllegalArgumentException("Not enough liquid left: " + currentPumpsRuntime.getKey().getIngredient().getName());
            }
        }


        for(Map.Entry<Pump, Integer> currentPumpRuntime : timeToRunPerPump.entrySet()) {
            if(longestRuntime == null || longestRuntime.getValue() < currentPumpRuntime.getValue()) {
                longestRuntime = currentPumpRuntime;
            }
        }
        timeToRunPerPump.remove(longestRuntime.getKey());
        this.longestIngredientPump = longestRuntime.getKey();
        this.longestPumpRunTime = longestRuntime.getValue();
        this.otherPumpTimings = timeToRunPerPump;
    }

    public Set<Pump> getUpdatedPumps() {
        return updatedPumps;
    }

    public int getLongestIngredientTime() {
        return longestPumpRunTime;
    }

    public Pump getLongestIngredientPump() {
        return longestIngredientPump;
    }

    public Set<PumpPhase> getPumpPhases() {
        Set<PumpPhase> pumpPhases = new HashSet<>();
        pumpPhases.add(new PumpPhase(0, this.longestPumpRunTime, this.longestIngredientPump));

        for(Map.Entry<Pump, Integer> ingredientPair : this.otherPumpTimings.entrySet()) {
            Pump currentPump = ingredientPair.getKey();
            int fullPumpOperatingTime = ingredientPair.getValue();

            //How often does the Pump need to be startet
            int pumpStarts = fullPumpOperatingTime / this.minimalPumpTime;
            if(pumpStarts == 0) {
                pumpStarts = 1;
            }
            int pumpIntervalOperatingTime = fullPumpOperatingTime / pumpStarts;
            int breakTime = (this.longestPumpRunTime - fullPumpOperatingTime) / (pumpStarts + 1);
            int nextPumpStart = breakTime;
            int lastPumpStop = 0;
            PumpPhase lastPumpPhase = null;
            for(int i = 1; i <= pumpStarts; i++) {
                if(nextPumpStart - lastPumpStop >= this.minimalBreakTime || lastPumpPhase == null) {
                    int currentPumpStart = nextPumpStart;
                    int currentPumpStop = currentPumpStart + pumpIntervalOperatingTime;
                    lastPumpPhase = new PumpPhase(currentPumpStart, currentPumpStop, currentPump);
                    pumpPhases.add(lastPumpPhase);
                    nextPumpStart = currentPumpStop + breakTime;
                    lastPumpStop = currentPumpStop;
                } else {
                    lastPumpPhase.addTime(pumpIntervalOperatingTime);
                    lastPumpStop += pumpIntervalOperatingTime;
                    nextPumpStart += pumpIntervalOperatingTime + breakTime;
                }
            }
        }
        return pumpPhases;
    }
}
