package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.service.cocktailfactory.PumpPhase;

import java.util.*;

public class PumpUpProductionStepWorker extends AbstractPumpingProductionStepWorker {

    public PumpUpProductionStepWorker(Set<Pump> requiredPumps) {
        Set<PumpPhase> pumpPhases = new HashSet<>();
        for(Pump pump : requiredPumps) {
            if(pump.isPumpedUp()) {
                continue;
            }
            int runtime = pump.getConvertMlToRuntime(pump.getTubeCapacityInMl());
            PumpPhase pumpPhase = new PumpPhase(0, runtime, pump);
            pumpPhases.add(pumpPhase);
        }
        this.setPumpPhases(pumpPhases);
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
