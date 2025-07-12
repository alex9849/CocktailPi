package net.alex9849.cocktailpi.service.pumps.cocktailfactory;

import lombok.Getter;
import net.alex9849.cocktailpi.model.pump.DcPump;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.pump.StepperPump;
import net.alex9849.cocktailpi.model.pump.Valve;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.productionstepworker.PumpStepIngredient;
import net.alex9849.motorlib.motor.AcceleratingStepper;

import java.util.*;
import java.util.stream.Collectors;

public class PumpTimingStepCalculator {
    private int longestPumpRunTime;
    private DcPump longestIngredientPump;
    private final Map<DcPump, Integer> otherPumpTimings;

    Map<Valve, Long> valvesToRequestedGrams;
    private final Map<StepperPump, Long> steppersToSteps;
    @Getter
    private final Map<Pump, Integer> remainingFillingLevelByPump;
    private final int minimalPumpTime;
    private final int minimalBreakTime;

    public PumpTimingStepCalculator(Set<PumpStepIngredient> pumpStepIngredients, int minimalPumpTime, int minimalBreakTime,
                                    Map<Pump, Integer> remainingFillingLevelByPump) {
        if(minimalPumpTime <= 0) {
            throw new IllegalArgumentException("minimalPumpTime needs to be at least 1!");
        }
        this.minimalPumpTime = minimalPumpTime;
        if(minimalBreakTime <= 0) {
            throw new IllegalArgumentException("minimalBreakTime needs to be at least 1!");
        }
        this.minimalBreakTime = minimalBreakTime;
        if(pumpStepIngredients.size() == 0) {
            throw new IllegalArgumentException("pumpStepIngredients must be non empty!");
        }
        this.remainingFillingLevelByPump = remainingFillingLevelByPump;
        this.valvesToRequestedGrams = new HashMap<>();
        this.steppersToSteps = new HashMap<>();
        this.longestPumpRunTime = 0;
        //Prioritize pumps with a low filling level
        pumpStepIngredients.forEach(x -> x.getApplicablePumps().sort(Comparator.comparingInt(Pump::getFillingLevelInMl)));

        Map<DcPump, Integer> timeToRunPerPump = new HashMap<>();

        for(PumpStepIngredient pumpStepIngredient : pumpStepIngredients) {
            int remainingAmountToFillInMl = pumpStepIngredient.getAmount();
            for(Pump pump : pumpStepIngredient.getApplicablePumps()) {
                if(remainingAmountToFillInMl <= 0) {
                    break;
                }
                int remainingFillingLevel = remainingFillingLevelByPump.getOrDefault(pump, pump.getFillingLevelInMl());
                if(remainingFillingLevel == 0) {
                    continue;
                }
                int amountToFillForPumpInMl;
                if(remainingFillingLevel > remainingAmountToFillInMl) {
                    amountToFillForPumpInMl = remainingAmountToFillInMl;
                    remainingFillingLevelByPump.compute(pump, (p, currentConsumption) -> {
                        if(currentConsumption == null) {
                            currentConsumption = p.getFillingLevelInMl();
                        }
                        return currentConsumption - amountToFillForPumpInMl;
                    });
                } else {
                    amountToFillForPumpInMl = remainingFillingLevel;
                    remainingFillingLevelByPump.put(pump, 0);
                }

                int timeToRun;
                if(pump instanceof DcPump dcPump) {
                    timeToRun = dcPump.getConvertMlToRuntime(amountToFillForPumpInMl);
                    timeToRunPerPump.put(dcPump, timeToRun);
                    if(timeToRun > longestPumpRunTime) {
                        longestPumpRunTime = timeToRun;
                        longestIngredientPump = dcPump;
                    }

                } else if (pump instanceof StepperPump stepperPump) {
                    int stepsToRun = (stepperPump.getStepsPerCl() * amountToFillForPumpInMl) / 10;
                    steppersToSteps.put(stepperPump, (long) stepsToRun);

                    AcceleratingStepper aStepper = stepperPump.getMotorDriver();
                    long cPos = aStepper.getCurrentPosition();
                    long cTarget = aStepper.getTargetPosition();
                    aStepper.setCurrentPosition(0);
                    aStepper.moveTo(stepsToRun);
                    timeToRun = (int) aStepper.estimateTimeTillCompletion();
                    aStepper.setCurrentPosition(cPos);
                    aStepper.moveTo(cTarget);
                    if(timeToRun > longestPumpRunTime) {
                        longestPumpRunTime = timeToRun;
                        longestIngredientPump = null;
                    }

                } else if (pump instanceof Valve valve) {
                    valvesToRequestedGrams.put(valve, (long) amountToFillForPumpInMl);

                } else {
                    throw new IllegalArgumentException("Unknown pump-type: " + pump.getClass().getName());
                }
                remainingAmountToFillInMl -= amountToFillForPumpInMl;
            }
            if(remainingAmountToFillInMl > 0) {
                throw new IllegalArgumentException("Not enough liquid left: " + pumpStepIngredient.getIngredient().getName());
            }
        }


        if(longestIngredientPump != null) {
            timeToRunPerPump.remove(longestIngredientPump);
        }
        this.otherPumpTimings = timeToRunPerPump;
    }

    public Set<PumpPhase> getPumpPhases() {
        Set<PumpPhase> pumpPhases = new HashSet<>();
        if(longestIngredientPump != null) {
            pumpPhases.add(new PumpPhase(0, this.longestPumpRunTime, this.longestIngredientPump));
        }

        for(Map.Entry<DcPump, Integer> ingredientPair : this.otherPumpTimings.entrySet()) {
            DcPump currentPump = ingredientPair.getKey();
            int fullPumpOperatingTime = ingredientPair.getValue();

            //How often does the Pump need to be started
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

    public Map<StepperPump, Long> getSteppersToComplete() {
        return steppersToSteps;
    }

    public Map<Valve, Long> getValvesToRequestedGrams() {
        return valvesToRequestedGrams;
    }

    public Set<Pump> getUsedPumps() {
        Set<Pump> pumps = new HashSet<>();
        pumps.addAll(getSteppersToComplete().keySet());
        pumps.addAll(getValvesToRequestedGrams().keySet());
        pumps.addAll(getPumpPhases().stream().map(PumpPhase::getPump).collect(Collectors.toSet()));
        return pumps;
    }
}
