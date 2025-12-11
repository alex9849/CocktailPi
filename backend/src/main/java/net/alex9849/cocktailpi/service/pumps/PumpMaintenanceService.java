package net.alex9849.cocktailpi.service.pumps;

import com.pi4j.exception.Pi4JException;
import net.alex9849.cocktailpi.model.gpio.PinResource;
import net.alex9849.cocktailpi.model.pump.*;
import net.alex9849.cocktailpi.model.pump.motortasks.DcMotorTask;
import net.alex9849.cocktailpi.model.pump.motortasks.PumpTask;
import net.alex9849.cocktailpi.model.pump.motortasks.StepperMotorTask;
import net.alex9849.cocktailpi.model.pump.motortasks.ValveTask;
import net.alex9849.cocktailpi.model.system.ErrorInfo;
import net.alex9849.cocktailpi.model.system.settings.ReversePumpSettings;
import net.alex9849.cocktailpi.payload.dto.system.settings.ReversePumpSettingsDto;
import net.alex9849.cocktailpi.repository.OptionsRepository;
import net.alex9849.cocktailpi.service.GpioService;
import net.alex9849.cocktailpi.service.WebSocketService;
import net.alex9849.cocktailpi.utils.PinUtils;
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
import java.util.concurrent.atomic.AtomicInteger;

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
    private GpioService gpioService;

    @Autowired
    private OptionsRepository optionsRepository;

    private final Logger logger = LoggerFactory.getLogger(PumpMaintenanceService.class);

    private final PumpTaskExecutor pumpTaskExecutor = PumpTaskExecutor.getInstance();
    private final ScheduledExecutorService scheduledTasksExecutor = Executors.newSingleThreadScheduledExecutor();
    private ReversePumpSettings reversePumpSettings;
    private ScheduledFuture<?> automaticPumpBackTask;
    private final Map<Long, Long> jobIdByPumpId = new HashMap<>();
    private final Map<Long, PumpTask> pumpTasksByJobId = new HashMap<>();
    private Map<Long, PumpJobState> lastState = new HashMap<>();
    private Direction direction = Direction.FORWARD;
    private IOutputPin directionPin;
    private AtomicInteger loadCellOccupied = new AtomicInteger(0);

    public synchronized void postConstruct() {
        try {
            configureReversePumpSettings(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void configureReversePumpSettings (boolean reschedulePumpBack) {
        if(getReversePumpingSettings().isEnable()) {
            setDirection(this.direction);
        }
        if(reschedulePumpBack) {
            this.reschedulePumpBack();
        }
    }

    private void setDirection (Direction direction) {
        ReversePumpSettings rps = getReversePumpingSettings();
        if (!rps.isEnable()) {
            throw new IllegalStateException("ReversePumpSettings is not enabled!");
        }
        directionPin = rps.getSettings().getDirectorPin().getOutputPin();
        boolean fwHigh = rps.getSettings().isForwardStateHigh();
        PinState pinState;
        if ((direction == Direction.FORWARD) == fwHigh) {
            pinState = PinState.HIGH;
        } else {
            pinState = PinState.LOW;
        }
        directionPin.digitalWrite(pinState);
        this.direction = direction;
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
            try {
                cancelByPumpId(pump.getId());
                if (pump.isCanPump()) {
                    pump.getMotorDriver().shutdown();
                }
            } catch (Pi4JException e) {
                ErrorInfo eInfo = new ErrorInfo(e);
                for (String msg : eInfo.getExceptionTraceMessages()) {
                    logger.error(msg);
                }
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
            ReversePumpSettings rps = getReversePumpingSettings();
            if(!rps.isEnable()) {
                callback.run();
                throw new IllegalArgumentException("Reverse pumping not enabled!");
            }
            if(advice.getType() == PumpAdvice.Type.PUMP_DOWN) {
                overshootMultiplier += rps.getSettings().getOvershoot() / 100d;
            }
            try {
                setDirection(Direction.BACKWARD);
            } catch (Exception e) {
                callback.run();
                throw e;
            }
            final Runnable oldCallback = callback;
            callback = () -> {
                synchronized (this) {
                    if (this.direction == Direction.BACKWARD && !this.anyPumpsRunning()) {
                        try {
                            setDirection(Direction.FORWARD);
                        } catch (Exception e) {
                            oldCallback.run();
                            throw e;
                        }
                    }
                    oldCallback.run();
                }
            };
        }

        Future<?> jobFuture;
        PumpTask pumpTask;
        Long prevJobId = jobIdByPumpId.get(pump.getId());
        boolean isPumpUpDown = advice.getType() == PumpAdvice.Type.PUMP_UP
                || advice.getType() == PumpAdvice.Type.PUMP_DOWN;

        if (pump instanceof DcPump dcPump) {
            if(loadCellOccupied.get() > 0) {
                callback.run();
                throw new IllegalArgumentException("Load cell occupied! A valve is running that requires genuine load cell data!");
            }
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

            pumpTask = new DcMotorTask(prevJobId, dcPump, this.direction, isPumpUpDown, timeToRun);
            pumpTask.addCompletionCallBack(callback);
            pumpTaskExecutor.submit(pumpTask);


        } else if (pump instanceof StepperPump stepperPump) {
            if(loadCellOccupied.get() > 0) {
                callback.run();
                throw new IllegalArgumentException("Load cell occupied! A valve is running that requires genuine load cell data!");
            }
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

            pumpTask = new StepperMotorTask(prevJobId, stepperPump, this.direction, isPumpUpDown, stepsToRun);
            pumpTask.addCompletionCallBack(callback);
            pumpTaskExecutor.submit(pumpTask);

        } else if (pump instanceof Valve valve) {
            long mlToPump;
            boolean requireLoadCell = false;
            switch (advice.getType()) {
                case PUMP_ML:
                    if(anyPumpsRunning()) {
                        callback.run();
                        throw new IllegalArgumentException("Load cell occupied! Other pumps are running currently.");
                    }
                    requireLoadCell = true;
                    mlToPump = advice.getAmount();
                    break;
                case PUMP_UP:
                    if(anyPumpsRunning()) {
                        callback.run();
                        throw new IllegalArgumentException("Load cell occupied! Other pumps are running currently.");
                    }
                    requireLoadCell = true;
                    mlToPump = Math.round(valve.getTubeCapacityInMl());
                    break;
                case RUN:
                    if(loadCellOccupied.get() > 0) {
                        callback.run();
                        throw new IllegalArgumentException("Load cell occupied! A valve is running that requires genuine load cell data!");
                    }
                    mlToPump = Long.MAX_VALUE;
                    break;
                default:
                    callback.run();
                    throw new IllegalArgumentException("Valve can't run perform advice: " + advice.getType());
            }

            Runnable finalCallback = callback;
            boolean finalRequireLoadCell = requireLoadCell;
            Runnable valveTaskCallback = () -> {
                if(finalRequireLoadCell) {
                    loadCellOccupied.decrementAndGet();
                }
                finalCallback.run();
            };
            if(requireLoadCell) {
                loadCellOccupied.incrementAndGet();
            }
            pumpTask = new ValveTask(valve, mlToPump, prevJobId, valve, isPumpUpDown);
            pumpTask.addCompletionCallBack(valveTaskCallback);
            pumpTaskExecutor.submit(pumpTask);

        } else {
            callback.run();
            throw new IllegalStateException("PumpType not known: " + pump.getClass().getName());
        }

        jobIdByPumpId.put(pump.getId(), pumpTask.getJobId());
        pumpTasksByJobId.put(pumpTask.getJobId(), pumpTask);
        return pumpTask.getJobId();
    }
    public synchronized void reschedulePumpBack() {
        if (automaticPumpBackTask != null) {
            automaticPumpBackTask.cancel(false);
            automaticPumpBackTask = null;
        }
        ReversePumpSettings rps = getReversePumpingSettings();
        if (rps == null || !rps.isEnable()) {
            return;
        }
        if (rps.getSettings().getAutoPumpBackTimer() == 0) {
            return;
        }
        long delay = rps.getSettings().getAutoPumpBackTimer();
        automaticPumpBackTask = scheduledTasksExecutor.scheduleAtFixedRate(() -> {
            List<Pump> allPumps = pumpDataService.getAllPumps();
            for (Pump pump : allPumps) {
                if (pump.isPumpedUp() && pump.isCanPumpUp() && pump.isCanControlDirection()) {
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

    public synchronized void setReversePumpingSettings(ReversePumpSettings settings) {
        optionsRepository.setOption("RPS_Enable", Boolean.valueOf(settings.isEnable()).toString());
        if (settings.isEnable()) {
            ReversePumpSettings.Config details = settings.getSettings();
            PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.PUMP_DIRECTION, null, details.getDirectorPin());
            optionsRepository.setOption("RPS_Overshoot", Integer.valueOf(details.getOvershoot()).toString());
            optionsRepository.setOption("RPS_AutoPumpBackTimer", Integer.valueOf(details.getAutoPumpBackTimer()).toString());
            optionsRepository.setOption("RPS_ForwardState", Boolean.valueOf(details.isForwardStateHigh()).toString());
            optionsRepository.setPinOption(REPO_KEY_PUMP_DIRECTION_PIN, details.getDirectorPin());

        } else {
            optionsRepository.delOption("RPS_Overshoot", false);
            optionsRepository.delOption("RPS_AutoPumpBackTimer", false);
            optionsRepository.delOption("RPS_ForwardState", false);
            optionsRepository.delOption(REPO_KEY_PUMP_DIRECTION_PIN, false);
            optionsRepository.setOption("RPS_Enable", Boolean.valueOf(settings.isEnable()).toString());
        }
        this.reversePumpSettings = null;
        configureReversePumpSettings(true);
        this.webSocketService.broadcastPumpLayout(pumpDataService.getAllPumps());
    }

    public synchronized ReversePumpSettings getReversePumpingSettings() {
        if(this.reversePumpSettings != null) {
            return this.reversePumpSettings;
        }
        ReversePumpSettings rps = new ReversePumpSettings();
        rps.setEnable(Boolean.parseBoolean(optionsRepository.getOption("RPS_Enable").orElse(null)));
        if (rps.isEnable()) {
            ReversePumpSettings.Config cfg = new ReversePumpSettings.Config();
            cfg.setOvershoot(Integer.parseInt(optionsRepository.getOption("RPS_Overshoot").orElse(null)));
            cfg.setAutoPumpBackTimer(Integer.parseInt(optionsRepository.getOption("RPS_AutoPumpBackTimer").orElse(null)));
            cfg.setForwardStateHigh(Boolean.parseBoolean(optionsRepository.getOption("RPS_ForwardState").orElse(null)));
            cfg.setDirectorPin(optionsRepository.getPinOption(REPO_KEY_PUMP_DIRECTION_PIN).orElse(null));
            if(cfg.getDirectorPin() == null) {
                rps.setEnable(false);
                return rps;
            }
            rps.setSettings(cfg);
        }
        this.reversePumpSettings  = rps;
        return this.reversePumpSettings;
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

    private synchronized Map<Long, PumpJobState> getJobStateMapByPumpId(boolean onlyDelta) {
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

    public ReversePumpSettings fromDto(ReversePumpSettingsDto.Request.Create dto) {
        ReversePumpSettings rps = new ReversePumpSettings();
        rps.setEnable(dto.isEnable());
        if(dto.isEnable()) {
            ReversePumpSettingsDto.Config.Request.Create cfgDto = dto.getSettings();
            ReversePumpSettings.Config cfg = new ReversePumpSettings.Config();
            cfg.setAutoPumpBackTimer(cfgDto.getAutoPumpBackTimer());
            cfg.setOvershoot(cfgDto.getOvershoot());
            cfg.setForwardStateHigh(cfgDto.isForwardStateHigh());
            cfg.setDirectorPin(gpioService.fromDto(cfgDto.getDirectorPin()));
            rps.setSettings(cfg);
        }
        return rps;
    }

    public void reloadPins() {
        configureReversePumpSettings(false);
    }
}
