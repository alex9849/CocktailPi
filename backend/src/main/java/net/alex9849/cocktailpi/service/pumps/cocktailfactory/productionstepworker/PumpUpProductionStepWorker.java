package net.alex9849.cocktailpi.service.pumps.cocktailfactory.productionstepworker;

import net.alex9849.cocktailpi.model.pump.DcPump;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.pump.StepperPump;
import net.alex9849.cocktailpi.model.pump.Valve;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.CocktailFactory;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.PumpPhase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PumpUpProductionStepWorker extends AbstractPumpingProductionStepWorker {

    public PumpUpProductionStepWorker(CocktailFactory cocktailFactory, Set<Pump> requiredPumps) {
        super(cocktailFactory);
        Set<PumpPhase> pumpPhases = new HashSet<>();
        Map<StepperPump, Long> steppersToSteps = new HashMap<>();
        Map<Valve, Long> valvesToGrams = new HashMap<>();
        for(Pump pump : requiredPumps) {
            if(pump.isPumpedUp()) {
                continue;
            }
            if(pump instanceof DcPump dcPump) {
                int runtime = dcPump.getConvertMlToRuntime(pump.getTubeCapacityInMl());
                PumpPhase pumpPhase = new PumpPhase(0, runtime, dcPump);
                pumpPhases.add(pumpPhase);
            } else if (pump instanceof StepperPump stepperPump) {
                steppersToSteps.put(stepperPump, (long) (stepperPump.getStepsPerCl() * (stepperPump.getTubeCapacityInMl() / 10)));
            } else if (pump instanceof Valve valve) {
                valvesToGrams.put(valve, Math.round(valve.getTubeCapacityInMl()));
            } else {
                throw new IllegalStateException("Unknown pump-type: " + pump.getClass().getName());
            }
        }
        this.setDcPumpPhases(pumpPhases);
        this.setSteppersToComplete(steppersToSteps);
        this.setValvesToRequestedGrams(valvesToGrams);
    }

    protected void onFinish() {
        getUsedPumps().forEach(p -> p.setPumpedUp(true));
        getCocktailFactory().requestPumpPersist(getUsedPumps());
        super.onFinish();
    }

    @Override
    public Map<Pump, Integer> getNotUsedLiquid() {
        return new HashMap<>();
    }
}
