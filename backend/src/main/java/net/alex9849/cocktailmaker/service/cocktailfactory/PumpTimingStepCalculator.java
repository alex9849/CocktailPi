package net.alex9849.cocktailmaker.service.cocktailfactory;

import net.alex9849.cocktailmaker.model.Pump;

import java.util.*;

public class PumpTimingStepCalculator {
    private final int longestIngredientTime;
    private final Pump longestIngredientPump;
    private final Map<Pump, Integer> otherPumpTimings;
    private final int minimalPumpTime;
    private final int minimalBreakTime;

    public PumpTimingStepCalculator(Map<Pump, Integer> pumpRuntimes, int minimalPumpTime, int minimalBreakTime) {
        if(minimalPumpTime <= 0) {
            throw new IllegalArgumentException("minimalPumpTime needs to be at least 1!");
        }
        this.minimalPumpTime = minimalPumpTime;
        if(minimalBreakTime <= 0) {
            throw new IllegalArgumentException("minimalBreakTime needs to be at least 1!");
        }
        this.minimalBreakTime = minimalBreakTime;
        if(pumpRuntimes.size() == 0) {
            throw new IllegalArgumentException("pumpRuntimes must be non empty!");
        }

        Map.Entry<Pump, Integer> longestRuntime = null;
        for(Map.Entry<Pump, Integer> currentPumpRuntime : pumpRuntimes.entrySet()) {
            if(longestRuntime == null || longestRuntime.getValue() < currentPumpRuntime.getValue()) {
                longestRuntime = currentPumpRuntime;
            }
        }
        pumpRuntimes.remove(longestRuntime.getKey());
        this.longestIngredientPump = longestRuntime.getKey();
        this.longestIngredientTime = longestRuntime.getValue();
        this.otherPumpTimings = pumpRuntimes;
    }

    public int getLongestIngredientTime() {
        return longestIngredientTime;
    }

    public Pump getLongestIngredientPump() {
        return longestIngredientPump;
    }

    public Map<Pump, Integer> getOtherPumpTimings() {
        return otherPumpTimings;
    }

    public Map<Pump, List<PumpPhase>> getPumpPhases() {
        Map<Pump, List<PumpPhase>> pumpPhases = new HashMap<>();
        pumpPhases.put(this.longestIngredientPump, Arrays.asList(new PumpPhase(0, this.longestIngredientTime, this.longestIngredientPump)));

        for(Map.Entry<Pump, Integer> ingredientPair : this.otherPumpTimings.entrySet()) {
            Pump currentPump = ingredientPair.getKey();
            int fullPumpOperatingTime = ingredientPair.getValue();
            pumpPhases.put(currentPump, new ArrayList<>());

            //How often does the Pump need to be startet
            int pumpStarts = fullPumpOperatingTime / this.minimalPumpTime;
            if(pumpStarts == 0) {
                pumpStarts = 1;
            }
            int pumpIntervalOperatingTime = fullPumpOperatingTime / pumpStarts;
            int breakTime = (this.longestIngredientTime - fullPumpOperatingTime) / (pumpStarts + 1);
            int nextPumpStart = breakTime;
            int lastPumpStop = 0;
            PumpPhase lastPumpPhase = null;
            for(int i = 1; i <= pumpStarts; i++) {
                if(nextPumpStart - lastPumpStop >= this.minimalBreakTime || lastPumpPhase == null) {
                    int currentPumpStart = nextPumpStart;
                    int currentPumpStop = currentPumpStart + pumpIntervalOperatingTime;
                    lastPumpPhase = new PumpPhase(currentPumpStart, currentPumpStop, currentPump);
                    pumpPhases.get(currentPump).add(lastPumpPhase);
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
