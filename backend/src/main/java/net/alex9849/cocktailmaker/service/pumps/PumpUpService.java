package net.alex9849.cocktailmaker.service.pumps;

import net.alex9849.cocktailmaker.model.pump.*;
import net.alex9849.cocktailmaker.payload.dto.settings.ReversePumpingSettings;
import net.alex9849.cocktailmaker.repository.OptionsRepository;
import net.alex9849.cocktailmaker.service.WebSocketService;
import net.alex9849.motorlib.AcceleratingStepper;
import net.alex9849.motorlib.DCMotor;
import net.alex9849.motorlib.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;

@Service
@Transactional
@EnableScheduling
public class PumpUpService {
    @Autowired
    private PumpDataService pumpDataService;

    @Autowired
    private PumpLockService pumpLockService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private OptionsRepository optionsRepository;

    private final Logger logger = LoggerFactory.getLogger(PumpUpService.class);

    private final ExecutorService liveTasksExecutor = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduledTasksExecutor = Executors.newSingleThreadScheduledExecutor();
    private ReversePumpingSettings.Full reversePumpSettings;
    private ScheduledFuture<?> automaticPumpBackTask;
    private final Map<Long, PumpTask> pumpingTasks = new HashMap<>();
    private Map<Long, RunningState> lastState = new HashMap<>();
    private Direction direction = Direction.FORWARD;

    public void postConstruct() {
        this.reversePumpSettings = this.getReversePumpingSettings();
        this.reschedulePumpBack();

    }

    @Scheduled(fixedDelay = 500)
    void performPumpStateUpdate() {
        for (Map.Entry<Long, RunningState> entry : getRunningState(true).entrySet()) {
            webSocketService.broadcastPumpRunningState(entry.getKey(), entry.getValue());
        }
    }

    public synchronized void stopAllPumps() {
        List<Pump> pumps = pumpDataService.getAllPumps();
        for (Pump pump : pumps) {
            cancelPumpUp(pump.getId());
            if (pump.isCanPump()) {
                pump.getMotorDriver().shutdown();
            }
        }
    }

    public synchronized boolean isPumpPumpingUp(long pumpId) {
        return pumpingTasks.containsKey(pumpId) && !pumpingTasks.get(pumpId).isRunInfinity();
    }

    public synchronized boolean isPumpRunning(long pumpId) {
        return this.pumpingTasks.containsKey(pumpId);
    }

    public synchronized void cancelPumpUp(long pumpId) {
        PumpTask pumpTask = this.pumpingTasks.remove(pumpId);
        if (pumpTask != null) {
            pumpTask.cancel();
        }
    }

    public synchronized void runPumpOrPerformPumpUp(Pump pump, PumpAdvice advice, Runnable callback) {
        if (callback == null) {
            callback = () -> {
            };
        }
        Direction direction = advice.getType() == PumpAdvice.Type.PUMP_DOWN ? Direction.BACKWARD : Direction.FORWARD;
        if (direction != this.direction && !this.pumpingTasks.isEmpty()) {
            callback.run();
            throw new IllegalArgumentException("One or more pumps are currently pumping into the other direction!");
        }

        if ((!pump.isCanPumpUp() && (advice.getType() == PumpAdvice.Type.PUMP_UP || advice.getType() == PumpAdvice.Type.PUMP_DOWN))
                || !pump.isCanPump()
        ) {
            callback.run();
            throw new IllegalArgumentException("Pump setup isn't completed yet!");
        }

        if (isPumpRunning(pump.getId())) {
            cancelPumpUp(pump.getId());
        }

        this.direction = direction;

        double overshootMultiplier = 1;
        if (advice.getType() == PumpAdvice.Type.PUMP_DOWN) {
            overshootMultiplier = reversePumpSettings.getSettings().getOvershoot();
        }


        if (pump instanceof DcPump dcPump) {
            DCMotor dcMotor = dcPump.getMotorDriver();
            dcMotor.setDirection(this.direction);
            dcMotor.setRunning(true);
            ScheduledFuture<?> stopTask;
            DcMotorTask task;
            long timeToRun = 0;

            switch (advice.getType()) {
                case PUMP_ML:
                    timeToRun = (dcPump.getTimePerClInMs() * advice.getAmount()) / 10;
                    break;
                case PUMP_UP:
                    timeToRun = (long) (dcPump.getTimePerClInMs() * dcPump.getTubeCapacityInMl()) / 10;
                case PUMP_DOWN:
                    timeToRun = (long) (timeToRun * overshootMultiplier);
                    break;
                case PUMP_TIME:
                    timeToRun = advice.getAmount() * 1000;
                    break;
                case RUN:
                    timeToRun = Long.MAX_VALUE;
                    break;
                case PUMP_STEPS:
                    throw new IllegalArgumentException("DcPump can't run certain number of steps!");
            }

            if (timeToRun == Long.MAX_VALUE) {
                task = new DcMotorTask(dcPump, this.direction, Long.MAX_VALUE);
                stopTask = scheduledTasksExecutor.schedule(() -> {
                }, 0, TimeUnit.MICROSECONDS);
            } else {
                long duration = (long) (dcPump.getTubeCapacityInMl() * dcPump.getTimePerClInMs() * overshootMultiplier / 10);
                task = new DcMotorTask(dcPump, this.direction, duration);
                task.setStart();
                stopTask = scheduledTasksExecutor.schedule(task, duration, TimeUnit.MILLISECONDS);
            }

            this.pumpingTasks.put(pump.getId(), new PumpTask(stopTask, dcPump, timeToRun == Long.MAX_VALUE, task, callback));
            task.cdl.countDown();

        } else if (pump instanceof StepperPump stepperPump) {

            long stepsToRun = 0;
            switch (advice.getType()) {
                case PUMP_ML:
                    stepsToRun = (stepperPump.getStepsPerCl() * advice.getAmount()) / 10;
                    break;
                case PUMP_UP:
                    stepsToRun = (long) (stepperPump.getStepsPerCl() * stepperPump.getTubeCapacityInMl()) / 10;
                case PUMP_DOWN:
                    stepsToRun = (long) (stepsToRun * overshootMultiplier);
                    break;
                case PUMP_STEPS:
                    stepsToRun = advice.getAmount();
                    break;
                case RUN:
                    stepsToRun = Long.MAX_VALUE;
                    break;
                case PUMP_TIME:
                    throw new IllegalArgumentException("DcPump can't run certain amount of time!");
            }

            StepperMotorTask task = new StepperMotorTask(stepperPump, this.direction, stepsToRun);
            Future<?> stopTask = liveTasksExecutor.submit(task);
            this.pumpingTasks.put(pump.getId(), new PumpTask(stopTask, stepperPump, stepsToRun == Long.MAX_VALUE, task, callback));
            task.cdl.countDown();

        } else {
            callback.run();
            throw new IllegalStateException("PumpType not known: " + pump.getClass().getName());
        }
    }

    private synchronized void onPumpRunTaskComplete(long pumpId) {
        PumpTask pumpTask = this.pumpingTasks.remove(pumpId);
        if (pumpTask != null) {
            pumpTask.doFinalize();
        }
    }

    public synchronized Direction getDirection() {
        return direction;
    }

    public synchronized void reschedulePumpBack() {
        if (automaticPumpBackTask != null) {
            automaticPumpBackTask.cancel(false);
            automaticPumpBackTask = null;
        }
        if (reversePumpSettings == null || !reversePumpSettings.isEnable()) {
            return;
        }
        if (reversePumpSettings.getSettings().getAutoPumpBackTimer() == 0) {
            return;
        }
        long delay = reversePumpSettings.getSettings().getAutoPumpBackTimer();
        automaticPumpBackTask = scheduledTasksExecutor.scheduleAtFixedRate(() -> {
            List<Pump> allPumps = pumpDataService.getAllPumps();
            for (Pump pump : allPumps) {
                if (pump.isPumpedUp() && pump.isCanPumpUp()) {
                    if (!pumpLockService.testAndAcquirePumpLock(pump.getId(), this)) {
                        logger.info("Can't perform pump-back for pump with ID " + pump.getId() + ": Pump is currently occupied!");
                        continue;
                    }
                    this.runPumpOrPerformPumpUp(pump, new PumpAdvice(PumpAdvice.Type.PUMP_DOWN, 0), () -> {
                        try {
                            pumpDataService.updatePump(pump);
                            webSocketService.broadcastPumpLayout(pumpDataService.getAllPumps());
                        } finally {
                            pumpLockService.releasePumpLock(pump.getId(), this);
                        }
                    });
                }
            }
        }, delay, delay, TimeUnit.SECONDS);
    }

    public synchronized void setReversePumpingSettings(ReversePumpingSettings.Full settings) {
        if (!pumpingTasks.isEmpty()) {
            throw new IllegalStateException("Pumps occupied!");
        }

        optionsRepository.setOption("RPSEnable", Boolean.valueOf(settings.isEnable()).toString());
        if (settings.isEnable()) {
            ReversePumpingSettings.Details details = settings.getSettings();
            optionsRepository.setOption("RPSOvershoot", Integer.valueOf(details.getOvershoot()).toString());
            optionsRepository.setOption("RPSAutoPumpBackTimer", Integer.valueOf(details.getAutoPumpBackTimer()).toString());
            ReversePumpingSettings.VoltageDirectorPin vdPin = details.getDirectorPin();
            if (pumpDataService.findByBcmPin(vdPin.getBcmPin()).isPresent()) {
                throw new IllegalArgumentException("BCM-Pin is already used by a pump!");
            }
            optionsRepository.setOption("RPSVDPinFwStateHigh", Boolean.valueOf(vdPin.isForwardStateHigh()).toString());
            optionsRepository.setOption("RPSVDPinBcm", Integer.valueOf(vdPin.getBcmPin()).toString());
        } else {
            optionsRepository.delOption("RPSOvershoot", false);
            optionsRepository.delOption("RPSAutoPumpBackTimer", false);
            optionsRepository.delOption("RPSVDPinFwStateHigh", false);
            optionsRepository.delOption("RPSVDPinBcm", false);
        }
        this.reversePumpSettings = settings;
        reschedulePumpBack();
    }

    public synchronized ReversePumpingSettings.Full getReversePumpingSettings() {
        ReversePumpingSettings.Full settings = new ReversePumpingSettings.Full();
        settings.setEnable(Boolean.parseBoolean(optionsRepository.getOption("RPSEnable")));
        if (settings.isEnable()) {
            ReversePumpingSettings.VoltageDirectorPin vdPin = new ReversePumpingSettings.VoltageDirectorPin();
            vdPin.setBcmPin(Integer.parseInt(optionsRepository.getOption("RPSVDPinBcm")));
            vdPin.setForwardStateHigh(Boolean.parseBoolean(optionsRepository.getOption("RPSVDPinFwStateHigh")));

            ReversePumpingSettings.Details details = new ReversePumpingSettings.Details();
            details.setDirectorPin(vdPin);
            details.setOvershoot(Integer.parseInt(optionsRepository.getOption("RPSOvershoot")));
            details.setAutoPumpBackTimer(Integer.parseInt(optionsRepository.getOption("RPSAutoPumpBackTimer")));
            settings.setSettings(details);
        }
        return settings;
    }

    public synchronized RunningState getRunningState(long pumpId) {
        RunningState runningState = new RunningState();
        runningState.setPercentage(0);
        runningState.setInPumpUp(false);
        runningState.setRunning(false);
        runningState.setForward(this.direction == Direction.FORWARD);
        runningState.setFinished(false);
        PumpTask pumpTask = this.pumpingTasks.get(pumpId);
        if (pumpTask != null) {
            runningState.setPercentage((int) (pumpTask.motorTaskRunnable.getPercentageCompleted() * 100));
            runningState.setInPumpUp(!pumpTask.runInfinity);
            runningState.setRunning(true);
        }
        return runningState;
    }

    private Map<Long, RunningState> getRunningState(boolean onlyDelta) {
        Map<Long, RunningState> stateMap = new HashMap<>();
        for (PumpTask task : this.pumpingTasks.values()) {
            RunningState runningState = new RunningState();
            runningState.setPercentage(task.motorTaskRunnable.getPercentageCompleted());
            runningState.setInPumpUp(!task.runInfinity);
            runningState.setRunning(true);
            runningState.setForward(this.direction == Direction.FORWARD);
            stateMap.put(task.pump.getId(), runningState);
        }
        if (!onlyDelta) {
            return stateMap;
        }
        Map<Long, RunningState> delta = new HashMap<>();

        for (Map.Entry<Long, RunningState> oldentry : this.lastState.entrySet()) {
            if (!stateMap.containsKey(oldentry.getKey())) {
                RunningState runningState = new RunningState();
                runningState.setPercentage(0);
                runningState.setInPumpUp(false);
                runningState.setRunning(false);
                runningState.setForward(this.direction == Direction.FORWARD);
                runningState.setFinished(true);
                delta.put(oldentry.getKey(), runningState);

            } else if (!oldentry.getValue().equals(stateMap.get(oldentry.getKey()))) {
                delta.put(oldentry.getKey(), stateMap.get(oldentry.getKey()));
            }
        }
        Set<Long> newKeysInSet = new HashSet<>(stateMap.keySet());
        newKeysInSet.removeAll(lastState.keySet());
        for (Long key : newKeysInSet) {
            delta.put(key, stateMap.get(key));
        }

        this.lastState = stateMap;
        return delta;

    }

    private static class PumpTask {
        private final Future<?> future;
        private final Pump pump;
        private final boolean runInfinity;
        private final MotorTaskRunnable motorTaskRunnable;
        private final Runnable callback;

        public PumpTask(Future<?> future, Pump pump, boolean runInfinity, MotorTaskRunnable motorTaskRunnable, Runnable callback) {
            this.future = future;
            this.pump = pump;
            this.runInfinity = runInfinity;
            this.motorTaskRunnable = motorTaskRunnable;
            this.callback = callback;
        }

        public double getPercentageCompleted() {
            return motorTaskRunnable.getPercentageCompleted();
        }

        public void cancel() {
            future.cancel(true);
            doFinalize();
        }

        public void doFinalize() {
            pump.getMotorDriver().shutdown();
            callback.run();
        }

        public boolean isRunInfinity() {
            return runInfinity;
        }
    }

    private interface MotorTaskRunnable extends Runnable {
        int getPercentageCompleted();
    }

    private class DcMotorTask implements MotorTaskRunnable {
        DcPump dcPump;
        CountDownLatch cdl;
        Direction direction;
        long startTime;
        boolean runInfinity;
        long duration;

        DcMotorTask(DcPump dcPump, Direction direction, long duration) {
            this.dcPump = dcPump;
            this.direction = direction;
            this.cdl = new CountDownLatch(1);
            this.runInfinity = duration == Long.MAX_VALUE;
            this.duration = duration;
        }

        public void setStart() {
            this.startTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            dcPump.setPumpedUp(direction == Direction.FORWARD);
            try {
                cdl.await();
                PumpUpService.this.cancelPumpUp(dcPump.getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getPercentageCompleted() {
            if (runInfinity) {
                return 0;
            }
            return (int) (((System.currentTimeMillis() - this.startTime) * 100) / duration);
        }
    }

    private class StepperMotorTask implements MotorTaskRunnable {
        StepperPump stepperPump;
        CountDownLatch cdl;
        Direction direction;
        long stepsToRun;
        boolean isRunInfinity;
        int percentageDone = 0;

        /**
         * @param stepsToRun Long.MAX_VALUE == unlimited
         */
        StepperMotorTask(StepperPump stepperPump, Direction direction, long stepsToRun) {
            this.stepperPump = stepperPump;
            this.direction = direction;
            this.cdl = new CountDownLatch(1);
            this.stepsToRun = stepsToRun;
            this.isRunInfinity = stepsToRun == Long.MAX_VALUE;
        }

        @Override
        public int getPercentageCompleted() {
            return percentageDone;
        }

        @Override
        public void run() {
            try {
                AcceleratingStepper driver = stepperPump.getMotorDriver();
                if (isRunInfinity) {
                    //Pick a very large number
                    stepsToRun = 10000000000L;
                } else if (stepsToRun == 0) {
                    stepsToRun = 1;
                }
                if (direction == Direction.BACKWARD) {
                    stepsToRun = -stepsToRun;
                }
                driver.move(stepsToRun);
                while (driver.distanceToGo() != 0) {
                    driver.run();
                    percentageDone = (int) (100 - ((driver.distanceToGo() * 100) / stepsToRun));
                    if (Thread.interrupted()) {
                        return;
                    }
                    Thread.yield();
                }
                driver.setEnable(false);
                stepperPump.setPumpedUp(direction == Direction.FORWARD);
                cdl.await();
                PumpUpService.this.onPumpRunTaskComplete(stepperPump.getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
