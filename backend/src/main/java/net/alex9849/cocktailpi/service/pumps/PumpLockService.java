package net.alex9849.cocktailpi.service.pumps;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PumpLockService {
    private static class LockData {
        Object owner;
        int times_acquired;
    }
    private final Map<Long, LockData> lockByPumpId = new HashMap<>();
    private LockData globalLock;

    public synchronized boolean isPumpLocked(long pumpId) {
        return globalLock != null || lockByPumpId.containsKey(pumpId);
    }
    public synchronized boolean canAcquirePumpLock(long pumpId, Object acquirer) {
        if(globalLock != null && globalLock.owner != acquirer) {
            return false;
        }
        return !lockByPumpId.containsKey(pumpId) || lockByPumpId.get(pumpId).owner == acquirer;
    }

    public synchronized void acquirePumpLock(long pumpId, Object acquirer) {
        if(!canAcquirePumpLock(pumpId, acquirer)) {
            throw new IllegalArgumentException("Pump locked by different service!");
        }
        LockData ld = lockByPumpId.computeIfAbsent(pumpId, p -> new LockData());
        ld.owner = acquirer;
        ld.times_acquired++;
    }

    public synchronized boolean testAndAcquirePumpLock(long pumpId, Object acquirer) {
        if(canAcquirePumpLock(pumpId, acquirer)) {
            acquirePumpLock(pumpId, acquirer);
            return true;
        }
        return false;
    }

    public synchronized void releasePumpLock(long pumpId, Object acquirer) {
        if(!canAcquirePumpLock(pumpId, acquirer)) {
            throw new IllegalArgumentException("Pump locked by different service!");
        }
        lockByPumpId.computeIfPresent(pumpId, (k, v) -> {
            if((--v.times_acquired) <= 0) {
                return null;
            }
            return v;
        });
    }

    public synchronized boolean canAcquireGlobal(Object acquirer) {
        if(globalLock != null && globalLock.owner != acquirer) {
            return false;
        }
        for (Map.Entry<Long, LockData> entry : lockByPumpId.entrySet()) {
            if(entry.getValue().owner != acquirer) {
                return false;
            }
        }
        return true;
    }

    public synchronized void acquireGlobal(Object acquirer) {
        if(!canAcquireGlobal(acquirer)) {
            throw new IllegalArgumentException("Pump locked by different service!");
        }
        if(globalLock == null) {
            globalLock = new LockData();
            globalLock.owner = acquirer;
        }
        globalLock.times_acquired++;
    }

    public synchronized boolean testAndAcquireGlobal(Object acquirer) {
        if(canAcquireGlobal(acquirer)) {
            acquireGlobal(acquirer);
            return true;
        }
        return false;
    }

    public synchronized void releaseGlobal(Object acquirer) {
        if(!canAcquireGlobal(acquirer)) {
            throw new IllegalArgumentException("Pump locked by different service!");
        }
        if(globalLock == null) {
            return;
        }
        globalLock.times_acquired--;
        if(globalLock.times_acquired <= 0) {
            globalLock = null;
        }
    }

}
