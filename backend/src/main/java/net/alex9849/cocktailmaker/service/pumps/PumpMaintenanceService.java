package net.alex9849.cocktailmaker.service.pumps;

import net.alex9849.cocktailmaker.model.pump.*;
import net.alex9849.cocktailmaker.model.pump.motortasks.DcMotorTask;
import net.alex9849.cocktailmaker.model.pump.motortasks.PumpTask;
import net.alex9849.cocktailmaker.model.pump.motortasks.StepperMotorTask;
import net.alex9849.cocktailmaker.payload.dto.settings.ReversePumpingSettings;
import net.alex9849.cocktailmaker.repository.OptionsRepository;
import net.alex9849.cocktailmaker.service.WebSocketService;
import net.alex9849.cocktailmaker.utils.PinUtils;
import net.alex9849.motorlib.motor.Direction;
import net.alex9849.motorlib.pin.IOutputPin;
import net.alex9849.motorlib.pin.PinState;
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
public class PumpMaintenanceService {
    public static final String REPO_KEY_PUMP_DIRECTION_PIN = "RPS_Direction_Pin";
    @Autowired
    private PumpDataService pumpDataService;

    @Autowired
    private PumpLockService pumpLockService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private PinUtils pinUtils;

    @Autowired
    private OptionsRepository optionsRepository;

    private final Logger logger = LoggerFactory.getLogger(PumpMaintenanceService.class);

    private final ExecutorService liveTasksExecutor = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduledTasksExecutor = Executors.newSingleThreadScheduledExecutor();
    private ReversePumpingSettings.Full reversePumpSettings;
    private ScheduledFuture<?> automaticPumpBackTask;
    private final Map<Long, Long> jobIdByPumpId = new HashMap<>();
    private final Map<Long, PumpTask> pumpTasksByJobId = new HashMap<>();
    private Map<Long, PumpJobState> lastState = new HashMap<>();
    private Direction direction = Direction.FORWARD;
    private IOutputPin directionPin;

    public synchronized void postConstruct() {
        this.reversePumpSettings = this.getReversePumpingSettings();
        if(reversePumpSettings.isEnable()) {
            directionPin = pinUtils.getBoardOutputPin(reversePumpSettings.getSettings().getDirectorPin().getBcmPin());
            directionPin.digitalWrite(direction == Direction.FORWARD ? PinState.HIGH : PinState.LOW);
        }
        this.reschedulePumpBack();

    }

    @Scheduled(fixedDelay = 500)
    void performPumpStateUpdate() {
        for (Map.Entry<Long, PumpJobState> entry : getJobStateMapByPumpId(true).entrySet()) {
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
        return !pumpTask.isFinished();
    }

    public synchronized void cancelByPumpId(long pumpId) {
        PumpTask pumpTask = getCurrentPumpTask(pumpId);
        if (pumpTask != null) {
            pumpTask.cancel();
        }
    }

    private synchronized boolean anyPumpsRunning() {
        return this.jobIdByPumpId.values().stream()
                .map(pumpTasksByJobId::get)
                .anyMatch(x -> !x.isFinished());
    }

    public synchronized long dispatchPumpJob(Pump pump, PumpAdvice advice, Runnable callback) {
        if (callback == null) {
            callback = () -> {};
        }
        Direction direction = advice.getType() == PumpAdvice.Type.PUMP_DOWN ? Direction.BACKWARD : Direction.FORWARD;

        if (direction != this.direction && anyPumpsRunning()) {
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
            cancelByPumpId(pump.getId());
        }

        double overshootMultiplier = 1;
        if (direction == Direction.BACKWARD) {
            if(!reversePumpSettings.isEnable()) {
                callback.run();
                throw new IllegalArgumentException("Reverse pumping not enabled!");
            }
            if(advice.getType() == PumpAdvice.Type.PUMP_DOWN) {
                overshootMultiplier += reversePumpSettings.getSettings().getOvershoot();
            }
            this.directionPin.digitalWrite(PinState.LOW);
            final Runnable oldCallback = callback;
            callback = () -> {
                synchronized (this) {
                    if (this.direction == Direction.BACKWARD && !this.anyPumpsRunning()) {
                        this.directionPin.digitalWrite(PinState.HIGH);
                    }
                    oldCallback.run();
                }
            };
        }

        this.direction = direction;

        Future<?> jobFuture;
        PumpTask pumpTask;
        Long prevJobId = jobIdByPumpId.get(pump.getId());
        boolean isPumpUpDown = advice.getType() == PumpAdvice.Type.PUMP_UP
                || advice.getType() == PumpAdvice.Type.PUMP_DOWN;

        if (pump instanceof DcPump dcPump) {

            long timeToRun = 0;
            switch (advice.getType()) {
                case PUMP_ML:
                    timeToRun = (dcPump.getTimePerClInMs() * advice.getAmount()) / 10;
                    break;
                case PUMP_DOWN:
                    timeToRun = (long) (dcPump.getTimePerClInMs() * dcPump.getTubeCapacityInMl()) / 10;
                    timeToRun = (long) (timeToRun * overshootMultiplier);
                    break;
                case PUMP_UP:
                    timeToRun = (long) (dcPump.getTimePerClInMs() * dcPump.getTubeCapacityInMl()) / 10;
                    break;
                case PUMP_TIME:
                    timeToRun = advice.getAmount();
                    break;
                case RUN:
                    timeToRun = Long.MAX_VALUE;
                    break;
                case PUMP_STEPS:
                    callback.run();
                    throw new IllegalArgumentException("DcPump can't run certain number of steps!");
            }

            if (timeToRun == Long.MAX_VALUE) {
                pumpTask = new DcMotorTask(prevJobId, dcPump, this.direction, isPumpUpDown, Long.MAX_VALUE, callback);
            } else {
                pumpTask = new DcMotorTask(prevJobId, dcPump, this.direction, isPumpUpDown, timeToRun, callback);
            }
            jobFuture = liveTasksExecutor.submit(pumpTask);


        } else if (pump instanceof StepperPump stepperPump) {

            long stepsToRun = 0;
            switch (advice.getType()) {
                case PUMP_ML:
                    stepsToRun = (stepperPump.getStepsPerCl() * advice.getAmount()) / 10;
                    break;
                case PUMP_UP:
                    stepsToRun = (long) (stepperPump.getStepsPerCl() * stepperPump.getTubeCapacityInMl()) / 10;
                    break;
                case PUMP_DOWN:
                    stepsToRun = (long) (stepperPump.getStepsPerCl() * stepperPump.getTubeCapacityInMl()) / 10;
                    stepsToRun = (long) (stepsToRun * overshootMultiplier);
                    break;
                case PUMP_STEPS:
                    stepsToRun = advice.getAmount();
                    break;
                case RUN:
                    stepsToRun = Long.MAX_VALUE;
                    break;
                case PUMP_TIME:
                    callback.run();
                    throw new IllegalArgumentException("DcPump can't run certain amount of time!");
            }

            pumpTask = new StepperMotorTask(prevJobId, stepperPump, this.direction, isPumpUpDown, stepsToRun, callback);
            jobFuture = liveTasksExecutor.submit(pumpTask);

        } else {
            callback.run();
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
                    this.dispatchPumpJob(pump, new PumpAdvice(PumpAdvice.Type.PUMP_DOWN, 0), () -> {
                        try {
                            pumpDataService.updatePump(pump);
                            webSocketService.broadcastPumpLayout(pumpDataService.getAllPumps());
                        } finally {
                            pumpLockService.releasePumpLock(pump.getId(), this);
                        }
                    });
                }
            }
        }, delay, delay, TimeUnit.MINUTES);
    }

    public synchronized void setReversePumpingSettings(ReversePumpingSettings.Full settings) {
        if (pumpTasksByJobId.values().stream().anyMatch(x -> !x.isFinished())) {
            throw new IllegalStateException("Pumps occupied!");
        }

        optionsRepository.setOption("RPS_Enable", Boolean.valueOf(settings.isEnable()).toString());
        if (settings.isEnable()) {
            ReversePumpingSettings.Details details = settings.getSettings();
            optionsRepository.setOption("RPS_Overshoot", Integer.valueOf(details.getOvershoot()).toString());
            optionsRepository.setOption("RPS_AutoPumpBackTimer", Integer.valueOf(details.getAutoPumpBackTimer()).toString());
            ReversePumpingSettings.VoltageDirectorPin vdPin = details.getDirectorPin();
            if (pumpDataService.findByBcmPin(vdPin.getBcmPin()).isPresent()) {
                throw new IllegalArgumentException("BCM-Pin is already used by a pump!");
            }
            optionsRepository.setOption(REPO_KEY_PUMP_DIRECTION_PIN, Integer.valueOf(vdPin.getBcmPin()).toString());

        } else {
            optionsRepository.delOption("RPS_Overshoot", false);
            optionsRepository.delOption("RPS_AutoPumpBackTimer", false);
            optionsRepository.delOption(REPO_KEY_PUMP_DIRECTION_PIN, false);
            //Pump.setGlobalDirectionPin(null);
        }
        this.reversePumpSettings = settings;
        if(reversePumpSettings.isEnable()) {
            directionPin = pinUtils.getBoardOutputPin(reversePumpSettings.getSettings().getDirectorPin().getBcmPin());
            directionPin.digitalWrite(direction == Direction.FORWARD ? PinState.HIGH : PinState.LOW);
        }

        reschedulePumpBack();
    }

    public synchronized ReversePumpingSettings.Full getReversePumpingSettings() {
        ReversePumpingSettings.Full settings = new ReversePumpingSettings.Full();
        settings.setEnable(Boolean.parseBoolean(optionsRepository.getOption("RPS_Enable")));
        if (settings.isEnable()) {
            ReversePumpingSettings.VoltageDirectorPin vdPin = new ReversePumpingSettings.VoltageDirectorPin();
            vdPin.setBcmPin(Integer.parseInt(optionsRepository.getOption(REPO_KEY_PUMP_DIRECTION_PIN)));

            ReversePumpingSettings.Details details = new ReversePumpingSettings.Details();
            details.setDirectorPin(vdPin);
            details.setOvershoot(Integer.parseInt(optionsRepository.getOption("RPS_Overshoot")));
            details.setAutoPumpBackTimer(Integer.parseInt(optionsRepository.getOption("RPS_AutoPumpBackTimer")));
            settings.setSettings(details);
        }
        return settings;
    }

    public synchronized PumpJobState getJobStateByPumpId(long pumpId) {
        PumpJobState pumpState = new PumpJobState();
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

    private Map<Long, PumpJobState> getJobStateMapByPumpId(boolean onlyDelta) {
        Map<Long, PumpJobState> stateMap = new HashMap<>();
        for (Long pumpId : this.jobIdByPumpId.keySet()) {
            PumpJobState pumpState = getJobStateByPumpId(pumpId);
            stateMap.put(pumpId, pumpState);
        }
        if (!onlyDelta) {
            return stateMap;
        }
        Map<Long, PumpJobState> delta = new HashMap<>();

        for (Map.Entry<Long, PumpJobState> oldentry : this.lastState.entrySet()) {
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
