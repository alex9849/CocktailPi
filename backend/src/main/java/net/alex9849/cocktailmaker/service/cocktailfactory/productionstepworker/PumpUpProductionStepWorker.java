package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.service.cocktailfactory.PumpPhase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PumpUpProductionStepWorker extends AbstractPumpingProductionStepWorker {

    public PumpUpProductionStepWorker(Set<DcPump> requiredPumps) {
        Set<PumpPhase> pumpPhases = new HashSet<>();
        for(DcPump pump : requiredPumps) {
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
    public Map<DcPump, Integer> getNotUsedLiquid() {
        return new HashMap<>();
    }
}
