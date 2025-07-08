package net.alex9849.cocktailpi.service.pumps;

import net.alex9849.motorlib.motor.AcceleratingStepper;
import net.openhft.affinity.AffinityLock;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class StepperTaskWorker extends Thread {
    private static StepperTaskWorker instance;
    private final List<Job> motorTasks;
    private final Object nrLockRequestedLock = new Object();
    private Integer nrLockRequested = 0;

    private StepperTaskWorker() {
        this.setPriority(MAX_PRIORITY);
        this.setDaemon(true);
        this.setName("StepperTaskWorker");
        motorTasks = new ArrayList<>();
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
            motorTasks.add(new Job(step, future));
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
            for(int i = 0; i < motorTasks.size(); i++) {
                Job job = motorTasks.get(i);
                if(job.step == step) {
                    job.future.complete(null);
                    motorTasks.remove(i);
                    break;
                }
            }
            synchronized (nrLockRequestedLock) {
                nrLockRequested--;
            }
            motorTasks.notify();
        }

    }

    @Override
    public void run() {
        try (AffinityLock al = AffinityLock.acquireCore()) {
            synchronized (motorTasks) {
                while (true) {
                    while (motorTasks.isEmpty() || nrLockRequested > 0) {
                        try {
                            motorTasks.wait();
                        } catch (InterruptedException ignored) {}
                    }
                    for(int i = 0; i < motorTasks.size(); i++) {
                        Job job = motorTasks.get(i);
                        if (job.step.distanceToGo() != 0) {
                            job.step.run();
                        }
                        if (job.step.distanceToGo() == 0) {
                            job.future.complete(null);
                            motorTasks.remove(i--);
                        }
                    }
                }
            }
        }
    }

    private static class Job {

        public Job(AcceleratingStepper step, CompletableFuture<Void> future) {
            this.step = step;
            this.future = future;
        }

        private final AcceleratingStepper step;
        private final CompletableFuture<Void> future;
    }
}
