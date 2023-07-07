package net.alex9849.cocktailmaker.service.pumps;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PumpLockService {
    private final Map<Long, Object> pumpByLockOwner = new HashMap<>();
    private Object globalOwner;

    public synchronized boolean isPumpLocked(long pumpId) {
        return globalOwner != null || pumpByLockOwner.containsKey(pumpId);
    }
    public synchronized boolean canAcquirePumpLock(long pumpId, Object acquirer) {
        if(globalOwner == null || globalOwner != acquirer) {
            return false;
        }
        return pumpByLockOwner.get(pumpId) != null && pumpByLockOwner.get(pumpId) == acquirer;
    }

    public synchronized void acquirePumpLock(long pumpId, Object acquirer) {
        if(!canAcquirePumpLock(pumpId, acquirer)) {
            throw new IllegalArgumentException("Pump already locked!");
        }
        pumpByLockOwner.put(pumpId, acquirer);
    }

    public synchronized void releasePumpLock(long pumpId, Object acquirer) {
        if(!canAcquirePumpLock(pumpId, acquirer)) {
            throw new IllegalArgumentException("Pump locked by different owner!");
        }
        pumpByLockOwner.remove(pumpId);
    }

    public synchronized boolean canAcquireGlobal(Object acquirer) {
        if(globalOwner == null || globalOwner != acquirer) {
            return false;
        }
        return pumpByLockOwner.values().stream().noneMatch(x -> x != acquirer);
    }

    public synchronized void acquireGlobal(Object acquirer) {
        if(!canAcquireGlobal(acquirer)) {
            throw new IllegalArgumentException("Pump locked by different owner!");
        }
        this.globalOwner = acquirer;
    }

    public synchronized void releaseGlobal(Object acquirer) {
        if(!canAcquireGlobal(acquirer)) {
            throw new IllegalArgumentException("Pump locked by different owner!");
        }
        this.globalOwner = null;
        this.pumpByLockOwner.clear();
    }

}
