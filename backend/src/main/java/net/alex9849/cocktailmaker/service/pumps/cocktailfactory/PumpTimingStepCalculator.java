package net.alex9849.cocktailmaker.service.pumps.cocktailfactory;

import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.model.pump.StepperPump;
import net.alex9849.cocktailmaker.service.pumps.cocktailfactory.productionstepworker.PumpStepIngredient;
import net.alex9849.motorlib.AcceleratingStepper;

import java.util.*;

public class PumpTimingStepCalculator {
    private int longestPumpRunTime;
    private DcPump longestIngredientPump;
    private final Map<DcPump, Integer> otherPumpTimings;

    private final Set<AcceleratingStepper> drivers;
    private final Set<Pump> updatedPumps;
    private final int minimalPumpTime;
    private final int minimalBreakTime;

    public PumpTimingStepCalculator(Set<PumpStepIngredient> pumpStepIngredients, int minimalPumpTime, int minimalBreakTime) {
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
        this.updatedPumps = new HashSet<>();
        this.drivers = new HashSet<>();
        this.longestPumpRunTime = 0;
        //Prioritize pumps with a low filling level
        pumpStepIngredients.forEach(x -> x.getApplicablePumps().sort(Comparator.comparingInt(Pump::getFillingLevelInMl)));

        Map<DcPump, Integer> timeToRunPerPump = new HashMap<>();

        for(PumpStepIngredient pumpStepIngredient : pumpStepIngredients) {
            int remainingAmountToFillInMl = pumpStepIngredient.getAmount();
            for(Pump pump : pumpStepIngredient.getApplicablePumps()) {
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
                    AcceleratingStepper accelStepper = stepperPump.getMotorDriver();
                    accelStepper.move(stepsToRun);
                    drivers.add(accelStepper);
                    timeToRun = (int) accelStepper.estimateTimeTillCompletion();
                    if(timeToRun > longestPumpRunTime) {
                        longestPumpRunTime = timeToRun;
                        longestIngredientPump = null;
                    }

                } else {
                    throw new IllegalArgumentException("Unknown pump-type: " + pump.getClass().getName());
                }

                this.updatedPumps.add(pump);
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

    public Set<AcceleratingStepper> getDrivers() {
        return drivers;
    }
}
