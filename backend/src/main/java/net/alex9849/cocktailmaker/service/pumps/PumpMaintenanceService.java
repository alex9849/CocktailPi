package net.alex9849.cocktailmaker.service.pumps;

import net.alex9849.cocktailmaker.model.pump.*;
import net.alex9849.cocktailmaker.model.pump.motortasks.DcMotorTask;
import net.alex9849.cocktailmaker.model.pump.motortasks.PumpTask;
import net.alex9849.cocktailmaker.model.pump.motortasks.StepperMotorTask;
import net.alex9849.cocktailmaker.payload.dto.settings.ReversePumpingSettings;
import net.alex9849.cocktailmaker.repository.OptionsRepository;
import net.alex9849.cocktailmaker.service.WebSocketService;
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
import java.util.function.Consumer;

@Service
@Transactional
@EnableScheduling
public class PumpMaintenanceService {
    @Autowired
    private PumpDataService pumpDataService;

    @Autowired
    private PumpLockService pumpLockService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private OptionsRepository optionsRepository;

    private final Logger logger = LoggerFactory.getLogger(PumpMaintenanceService.class);

    private final ExecutorService liveTasksExecutor = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduledTasksExecutor = Executors.newSingleThreadScheduledExecutor();
    private ReversePumpingSettings.Full reversePumpSettings;
    private ScheduledFuture<?> automaticPumpBackTask;
    private final Map<Long, Long> jobIdByPumpId = new HashMap<>();
    private final Map<Long, PumpTask> pumpTasksByJobId = new HashMap<>();
    private Map<Long, PumpState> lastState = new HashMap<>();
    private Direction direction = Direction.FORWARD;

    public void postConstruct() {
        this.reversePumpSettings = this.getReversePumpingSettings();
        this.reschedulePumpBack();

    }

    @Scheduled(fixedDelay = 500)
    void performPumpStateUpdate() {
        for (Map.Entry<Long, PumpState> entry : getPumpState(true).entrySet()) {
            webSocketService.broadcastPumpRunningState(entry.getKey(), entry.getValue());
        }
    }

    public synchronized void stopAllPumps() {
        List<Pump> pumps = pumpDataService.getAllPumps();
        for (Pump pump : pumps) {
            cancelByPumpId(pump.getId());
            if (pump.isCanPump()) {
                pump.getMotorDriver().shutdown();
            }
        }
    }

    private synchronized PumpTask getCurrentPumpTask(long pumpId) {
        Long jobId = jobIdByPumpId.get(pumpId);
        if(jobId == null) {
            return null;
        }
        return pumpTasksByJobId.get(jobId);
    }

    public synchronized boolean isPumpRunning(long pumpId) {
        PumpTask pumpTask = getCurrentPumpTask(pumpId);
        if(pumpTask == null) {
            return false;
        }
        return pumpTask.isFinished();
    }

    public synchronized void cancelByPumpId(long pumpId) {
        PumpTask pumpTask = getCurrentPumpTask(pumpId);
        if (pumpTask != null) {
            pumpTask.cancel();
        }
    }

    public synchronized long runPumpOrPerformPumpUp(Pump pump, PumpAdvice advice, Consumer<Optional<PumpState.RunningState>> callback) {
        if (callback == null) {
            callback = (x) -> {
            };
        }
        Direction direction = advice.getType() == PumpAdvice.Type.PUMP_DOWN ? Direction.BACKWARD : Direction.FORWARD;
        boolean anyPumpRunning = this.jobIdByPumpId.entrySet().stream()
                .filter(x -> x.getKey() != pump.getId())
                .map(Map.Entry::getValue)
                .map(pumpTasksByJobId::get)
                .anyMatch(x -> !x.isFinished());

        if (direction != this.direction && anyPumpRunning) {
            callback.accept(Optional.empty());
            throw new IllegalArgumentException("One or more pumps are currently pumping into the other direction!");
        }

        if ((!pump.isCanPumpUp() && (advice.getType() == PumpAdvice.Type.PUMP_UP || advice.getType() == PumpAdvice.Type.PUMP_DOWN))
                || !pump.isCanPump()
        ) {
            callback.accept(Optional.empty());
            throw new IllegalArgumentException("Pump setup isn't completed yet!");
        }

        if (isPumpRunning(pump.getId())) {
            cancelByPumpId(pump.getId());
        }

        this.direction = direction;

        double overshootMultiplier = 1;
        if (advice.getType() == PumpAdvice.Type.PUMP_DOWN) {
            overshootMultiplier = reversePumpSettings.getSettings().getOvershoot();
        }

        Future<?> jobFuture;
        PumpTask pumpTask;
        Long prevJobId = jobIdByPumpId.get(pump.getId());

        if (pump instanceof DcPump dcPump) {
            DCMotor dcMotor = dcPump.getMotorDriver();
            dcMotor.setDirection(this.direction);
            dcMotor.setRunning(true);
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
                pumpTask = new DcMotorTask(prevJobId, dcPump, this.direction, Long.MAX_VALUE, callback);
                jobFuture = scheduledTasksExecutor.schedule(() -> {
                }, 0, TimeUnit.MICROSECONDS);
            } else {
                long duration = (long) (dcPump.getTubeCapacityInMl() * dcPump.getTimePerClInMs() * overshootMultiplier / 10);
                pumpTask = new DcMotorTask(prevJobId, dcPump, this.direction, duration, callback);
                jobFuture = scheduledTasksExecutor.schedule(pumpTask, duration, TimeUnit.MILLISECONDS);
            }

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

            pumpTask = new StepperMotorTask(prevJobId, stepperPump, this.direction, stepsToRun, callback);
            jobFuture = liveTasksExecutor.submit(pumpTask);

        } else {
            callback.accept(Optional.empty());
            throw new IllegalStateException("PumpType not known: " + pump.getClass().getName());
        }

        jobIdByPumpId.put(pump.getId(), pumpTask.getJobId());
        pumpTasksByJobId.put(pumpTask.getJobId(), pumpTask);
        pumpTask.readify(jobFuture);
        return pumpTask.getJobId();
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
                    this.runPumpOrPerformPumpUp(pump, new PumpAdvice(PumpAdvice.Type.PUMP_DOWN, 0), (x) -> {
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
        if (pumpTasksByJobId.values().stream().anyMatch(x -> !x.isFinished())) {
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

    public synchronized PumpState getRunningStateByPumpId(long pumpId) {
        PumpState pumpState = new PumpState();
        PumpTask pumpTask = getCurrentPumpTask(pumpId);
        if(pumpTask == null) {
            return pumpState;
        }
        if (pumpTask.isFinished()) {
            pumpState.setLastJobId(pumpTask.getJobId());
            return pumpState;
        }

        pumpState.setLastJobId(pumpTask.getPrevJobId());
        pumpState.setRunningState(pumpTask.getRunningState());
        return pumpState;
    }

    private Map<Long, PumpState> getPumpState(boolean onlyDelta) {
        Map<Long, PumpState> stateMap = new HashMap<>();
        for (Long pumpId : this.jobIdByPumpId.keySet()) {
            PumpState pumpState = getRunningStateByPumpId(pumpId);
            stateMap.put(pumpId, pumpState);
        }
        if (!onlyDelta) {
            return stateMap;
        }
        Map<Long, PumpState> delta = new HashMap<>();

        for (Map.Entry<Long, PumpState> oldentry : this.lastState.entrySet()) {
            if (!oldentry.getValue().equals(stateMap.get(oldentry.getKey()))) {
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

    public synchronized JobMetrics getJobMetrics(long id) {
        PumpTask pumpTask = pumpTasksByJobId.get(id);
        if(pumpTask == null) {
            return null;
        }
        return pumpTask.getJobMetrics();
    }
}
