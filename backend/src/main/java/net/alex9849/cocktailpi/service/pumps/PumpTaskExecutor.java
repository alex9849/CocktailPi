package net.alex9849.cocktailpi.service.pumps;


import lombok.Setter;
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
    private final static long waitTimeBetweenTasks = 300;
    private final List<List<PumpTask>> pumpTaskGroups = new ArrayList<>();
    @Setter
    private Integer powerLimit = null;

    private PumpTaskExecutor() {}

    public static PumpTaskExecutor getInstance() {
        if (PumpTaskExecutor.instance == null) {
            PumpTaskExecutor.instance = new PumpTaskExecutor();
            PumpTaskExecutor.instance.setDaemon(true);
            PumpTaskExecutor.instance.setName("PumpTaskExecutor");
            PumpTaskExecutor.instance.start();
        }
        return PumpTaskExecutor.instance;
    }

    private int getPowerConsumption(List<PumpTask> pumpTasks) {
        return pumpTasks.stream().mapToInt(x -> x.getPump().getPowerConsumption()).sum();
    }

    private void cleanupSchedule(int currentIdx) {
        for(List<PumpTask> pumpTasks : pumpTaskGroups) {
            pumpTasks.removeIf(PumpTask::isFinished);
        }
        for (int i = 0; i < pumpTaskGroups.size(); i++) {
            List<PumpTask> groupToFill = pumpTaskGroups.get(i);
            int groupPConsumption =  getPowerConsumption(groupToFill);
            for (int j = 0; j < pumpTaskGroups.size(); j++) {
                // Do not move tasks away from the current group
                if(i >= j || j == currentIdx) {
                    continue;
                }
                List<PumpTask> distributeGroup = pumpTaskGroups.get(j);
                for(PumpTask pumpTask : new ArrayList<>(distributeGroup)) {
                    if(powerLimit != null && groupPConsumption + pumpTask.getPump().getPowerConsumption() > powerLimit) {
                        continue;
                    }
                    distributeGroup.remove(pumpTask);
                    groupToFill.add(pumpTask);
                    groupPConsumption += pumpTask.getPump().getPowerConsumption();
                }
            }
        }
        pumpTaskGroups.removeIf(List::isEmpty);
    }


    public void run() {
        synchronized (pumpTaskGroups) {
            int currentGroupIdx = 0;
            try {
                while (true) {
                    long timeTillSuspend = 10_000;
                    long timeRemaining = timeTillSuspend;

                    while (pumpTaskGroups.isEmpty()) {
                        pumpTaskGroups.wait();
                    }

                    boolean suspendGroup = false;
                    List<PumpTask> currentGroup = pumpTaskGroups.get(currentGroupIdx % pumpTaskGroups.size());
                    while (!suspendGroup) {
                        for (PumpTask pumpTask : currentGroup) {
                            pumpTask.signalStart();
                        }
                        long startWaitTime = System.currentTimeMillis();
                        pumpTaskGroups.wait(timeRemaining);
                        timeRemaining = timeRemaining - (System.currentTimeMillis() - startWaitTime);
                        if (timeRemaining <= 0) {
                            timeRemaining = timeTillSuspend;
                            suspendGroup = true;
                        }
                        for(List<PumpTask> pumpTasks : pumpTaskGroups) {
                            pumpTasks.removeIf(PumpTask::isFinished);
                        }
                        cleanupSchedule(currentGroupIdx);
                        suspendGroup &= pumpTaskGroups.size() > 1;
                        if (currentGroup.isEmpty()) {
                            break;
                        }
                        if (suspendGroup) {
                            List<Callable<Boolean>> suspendTasks = new ArrayList<>();
                            for (PumpTask pumpTask : currentGroup) {
                                suspendTasks.add(pumpTask::suspend);
                            }
                            try {
                                List<Future<Boolean>> futures = executor.invokeAll(suspendTasks);
                                for (Future<Boolean> future : futures) {
                                    try {
                                        future.get();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                //noinspection BusyWait
                                Thread.sleep(waitTimeBetweenTasks);
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
            List<PumpTask> addGroup = new ArrayList<>();
            addGroup.add(task);
            pumpTaskGroups.add(addGroup);
            this.pumpTaskGroups.notify();
        }
        Future<?> future = executor.submit(task);
        task.readify(future);
    }
}
