package net.alex9849.cocktailpi.service.pumps;


import net.alex9849.cocktailpi.model.pump.motortasks.PumpTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PumpTaskExecutor extends Thread {
    private static PumpTaskExecutor instance;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final List<List<PumpTask>> pumpTaskGroups = new ArrayList<>();
    private Integer powerLimit = null;

    private PumpTaskExecutor() {}

    public static PumpTaskExecutor getInstance() {
        if (PumpTaskExecutor.instance == null) {
            PumpTaskExecutor.instance = new PumpTaskExecutor();
            PumpTaskExecutor.instance.setDaemon(true);
            PumpTaskExecutor.instance.start();
        }
        return PumpTaskExecutor.instance;
    }

    public void setPowerLimit(int milliWatt) {
        this.powerLimit = milliWatt;
    }

    public void delPowerLimit() {
        this.powerLimit = null;
    }

    public void cleanUpFinishedPumpTasks() {
        for (List<PumpTask> pumpTasks : new ArrayList<>(pumpTaskGroups)) {
            pumpTasks.removeIf(PumpTask::isFinished);
        }
        pumpTaskGroups.removeIf(List::isEmpty);
    }


    public void run() {
        synchronized (pumpTaskGroups) {
            int currentGroupIdx = 0;
            try {
                while (true) {
                    long timeTillSuspend = 10_000;

                    while (pumpTaskGroups.isEmpty()) {
                        pumpTaskGroups.wait();
                    }

                    List<PumpTask> currentGroup = pumpTaskGroups.get(currentGroupIdx);
                    boolean suspendGroup = false;
                    long startWaitTime = System.currentTimeMillis();
                    while (!suspendGroup) {
                        for (PumpTask pumpTask : currentGroup) {
                            pumpTask.signalStart();
                        }
                        pumpTaskGroups.wait(timeTillSuspend);
                        suspendGroup = System.currentTimeMillis() - startWaitTime >= timeTillSuspend;
                        cleanUpFinishedPumpTasks();
                        suspendGroup &= pumpTaskGroups.size() > 1;
                        if (currentGroup.isEmpty()) {
                            break;
                        }
                        if (suspendGroup) {
                            List<Callable<Void>> suspendTasks = new ArrayList<>();
                            for (PumpTask pumpTask : currentGroup) {
                                suspendTasks.add(pumpTask::suspend);
                            }
                            try {
                                List<Future<Void>> futures = executor.invokeAll(suspendTasks);
                                for (Future<Void> future : futures) {
                                    try {
                                        future.get();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }
                    if(pumpTaskGroups.isEmpty()) {
                        currentGroupIdx = 0;
                    } else {
                        currentGroupIdx = (currentGroupIdx + 1) % pumpTaskGroups.size();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void submit(PumpTask task) {
        synchronized (this.pumpTaskGroups) {
            Runnable completionCallback = () -> {
                synchronized (this.pumpTaskGroups) {
                    this.pumpTaskGroups.notify();
                }
            };
            task.addCompletionCallBack(completionCallback);

            List<PumpTask> addGroup = null;
            for (List<PumpTask> group : pumpTaskGroups) {
                if (this.powerLimit != null) {
                    int groupConsumption = group.stream().mapToInt(x -> x.getPump().getPowerConsumption()).sum();
                    if (groupConsumption + task.getPump().getPowerConsumption() > this.powerLimit) {
                        continue;
                    }
                }
                addGroup = group;
                break;
            }
            if (addGroup == null) {
                addGroup = new ArrayList<>();
                this.pumpTaskGroups.add(addGroup);
            }
            addGroup.add(task);
            this.pumpTaskGroups.notify();
        }
        Future<?> future = executor.submit(task);
        task.readify(future);
    }
}
