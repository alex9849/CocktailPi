package net.alex9849.cocktailpi.service.pumps;

import net.alex9849.motorlib.motor.AcceleratingStepper;
import net.openhft.affinity.AffinityLock;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class StepperTaskWorker extends Thread {
    private static StepperTaskWorker instance;
    private final HashMap<AcceleratingStepper, CompletableFuture<Void>> motorTasks;
    private final Object nrLockRequestedLock = new Object();
    private Integer nrLockRequested = 0;

    private StepperTaskWorker() {
        this.setPriority(MAX_PRIORITY);
        this.setDaemon(true);
        motorTasks = new HashMap<>();
        this.start();
    }

    public synchronized static StepperTaskWorker getInstance() {
        if (instance == null) {
            instance = new StepperTaskWorker();
        }
        return instance;
    }

    public Future<Void> submitTask(AcceleratingStepper step) {
        synchronized (nrLockRequestedLock) {
            nrLockRequested++;
        }
        synchronized (motorTasks) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            motorTasks.put(step, future);
            synchronized (nrLockRequestedLock) {
                nrLockRequested--;
            }
            motorTasks.notify();
            return future;
        }
    }

    public void cancelTask(AcceleratingStepper step) {
        synchronized (nrLockRequestedLock) {
            nrLockRequested++;
        }
        synchronized (motorTasks) {
            CompletableFuture<Void> future = motorTasks.remove(step);
            if (future != null) {
                future.complete(null);
            }
            synchronized (nrLockRequestedLock) {
                nrLockRequested--;
            }
            motorTasks.notify();
        }

    }

    @Override
    public void run() {
        Set<AcceleratingStepper> toRemove = new HashSet<>();
        try (AffinityLock al = AffinityLock.acquireCore()) {
            synchronized (motorTasks) {
                while (true) {
                    while (motorTasks.isEmpty() || nrLockRequested > 0) {
                        try {
                            motorTasks.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                    for (AcceleratingStepper step : motorTasks.keySet()) {
                        if (step.distanceToGo() != 0) {
                            step.run();
                        }
                        if (step.distanceToGo() == 0) {
                            toRemove.add(step);
                        }
                    }
                    for (AcceleratingStepper step : toRemove) {
                        CompletableFuture<Void> future = motorTasks.remove(step);
                        future.complete(null);
                    }
                    toRemove.clear();
                }
            }
        }
    }
}
