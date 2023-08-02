package net.alex9849.cocktailmaker.service.pumps.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.model.pump.StepperPump;
import net.alex9849.cocktailmaker.service.pumps.cocktailfactory.PumpPhase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PumpUpProductionStepWorker extends AbstractPumpingProductionStepWorker {

    public PumpUpProductionStepWorker(Set<Pump> requiredPumps) {
        Set<PumpPhase> pumpPhases = new HashSet<>();
        Map<StepperPump, Double> mlByStepperPump = new HashMap<>();
        for(Pump pump : requiredPumps) {
            if(pump.isPumpedUp()) {
                continue;
            }
            if(pump instanceof DcPump dcPump) {
                int runtime = dcPump.getConvertMlToRuntime(pump.getTubeCapacityInMl());
                PumpPhase pumpPhase = new PumpPhase(0, runtime, dcPump);
                pumpPhases.add(pumpPhase);
            } else if (pump instanceof StepperPump stepperPump) {
                mlByStepperPump.put(stepperPump, stepperPump.getTubeCapacityInMl());
            } else {
                throw new IllegalStateException("Unknown pump-type: " + pump.getClass().getName());
            }
        }
        this.setDcPumpPhases(pumpPhases);
        this.setDriversToComplete(mlByStepperPump);
    }

    protected void onFinish() {
        getUsedPumps().forEach(p -> p.setPumpedUp(true));
        super.onFinish();
    }

    @Override
    public Map<Pump, Integer> getNotUsedLiquid() {
        return new HashMap<>();
    }
}
