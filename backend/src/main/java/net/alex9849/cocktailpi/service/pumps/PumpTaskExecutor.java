package net.alex9849.cocktailpi.service.pumps;


import net.alex9849.cocktailpi.model.pump.motortasks.PumpTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PumpTaskExecutor extends Thread {
    private static PumpTaskExecutor instance;
    private final ExecutorService liveTasksExecutor = Executors.newCachedThreadPool();
    private List<List<PumpTask>> pumpTaskGroups = new ArrayList<>();
    private int currentGroupIdx = 0;
    private Integer powerLimit = null;

    private PumpTaskExecutor() {}

    public static PumpTaskExecutor getInstance() {
        if(PumpTaskExecutor.instance == null) {
            PumpTaskExecutor.instance = new PumpTaskExecutor();
        }
        return PumpTaskExecutor.instance;
    }

    public void setPowerLimit(int milliWatt) {
        this.powerLimit = milliWatt;
    }

    public void delPowerLimit() {
        this.powerLimit = null;
    }

    public void deleteFinishedPumpTasks() {
    }


    public void run() {
        while (true) {

        }
    }



    public synchronized Future<?> submit(PumpTask task) {
        for(List<PumpTask> group : pumpTaskGroups) {
            int groupConsumption = group.stream().mapToInt(x -> x.getPump().getPowerConsumption()).sum();
            if(groupConsumption + task.getPump().getPowerConsumption() > this.powerLimit) {
                continue;
            }
            group.add(task);
            return null;
        }
        List<PumpTask> newGroup = new ArrayList<>();
        newGroup.add(task);
        this.pumpTaskGroups.add(newGroup);
        return null;
    }
}
