package net.alex9849.cocktailmaker.service.pumps;

import net.alex9849.cocktailmaker.hardware.IGpioController;
import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.model.pump.StepperPump;
import net.alex9849.cocktailmaker.payload.dto.settings.ReversePumpingSettings;
import net.alex9849.cocktailmaker.repository.OptionsRepository;
import net.alex9849.cocktailmaker.service.WebSocketService;
import net.alex9849.motorlib.AcceleratingStepper;
import net.alex9849.motorlib.DCMotor;
import net.alex9849.motorlib.Direction;
import net.alex9849.motorlib.IMotor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;

@Service
@Transactional
public class PumpUpService {
    @Autowired
    private PumpService pumpService;

    @Autowired
    private PumpLockService pumpLockService;

    @Autowired
    private OptionsRepository optionsRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private IGpioController gpioController;

    private final ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
    private ReversePumpingSettings.Full reversePumpSettings;
    private ScheduledFuture<?> automaticPumpBackTask;
    private final Map<Long, Map.Entry<IMotor, ScheduledFuture<?>>> pumpingUpTasks = new HashMap<>();
    private Direction direction = Direction.FORWARD;

    public void postConstruct() {
        this.reversePumpSettings = this.getReversePumpingSettings();
        this.reschedulePumpBack();
    }

    public synchronized boolean isPumpPumpingUp(Pump pump) {
        return this.pumpingUpTasks.containsKey(pump.getId());
    }

    public synchronized void cancelPumpUp(Pump pump) {
        Map.Entry<IMotor, ScheduledFuture<?>> pumpUpTask = this.pumpingUpTasks.remove(pump.getId());
        if (pumpUpTask != null) {
            pumpUpTask.getValue().cancel(true);
            pumpUpTask.getKey().shutdown();

            pumpLockService.releasePumpLock(pump.getId(), this);
            pumpService.updatePump(pump);
        }
    }

    public synchronized void pumpBackOrUp(Pump pump, Direction direction) {
        if (!pumpLockService.testAndAcquirePumpLock(pump.getId(), this)) {
            throw new IllegalArgumentException("Pump is currently occupied!");
        }

        if (direction != this.direction && !this.pumpingUpTasks.isEmpty()) {
            pumpLockService.releasePumpLock(pump.getId(), this);
            throw new IllegalArgumentException("One or more pumps are currently pumping into the other direction!");
        }

        if (isPumpRunning(pump.getId())) {
            pumpLockService.releasePumpLock(pump.getId(), this);
            throw new IllegalArgumentException("Pump is already running!");
        }
        this.direction = direction;

        double overshootMultiplier = 1;
        if (this.direction == Direction.BACKWARD) {
            overshootMultiplier = reversePumpSettings.getSettings().getOvershoot();
        }


        if (pump instanceof DcPump) {
            DcPump dcPump = (DcPump) pump;
            DCMotor dcMotor = dcPump.getMotorDriver();
            dcMotor.setDirection(this.direction);
            DcMotorTask task = new DcMotorTask(dcPump, this.direction);
            long duration = (long) (dcPump.getTubeCapacityInMl() * dcPump.getTimePerClInMs() * overshootMultiplier);
            dcMotor.setRunning(true);
            ScheduledFuture<?> stopTask = executor.scheduleWithFixedDelay(task, Duration.ofMillis(duration));
            this.pumpingUpTasks.put(pump.getId(), Map.entry(dcMotor, stopTask));
            task.cdl.countDown();

        } else if (pump instanceof StepperPump) {
            StepperPump stepperPump = (StepperPump) pump;
            StepperMotorTask task = new StepperMotorTask(stepperPump, this.direction, overshootMultiplier);
            ScheduledFuture<?> stopTask = executor.scheduleWithFixedDelay(task, Duration.ZERO);
            this.pumpingUpTasks.put(pump.getId(), Map.entry(stepperPump.getMotorDriver(), stopTask));
            task.cdl.countDown();

        } else {
            pumpLockService.releasePumpLock(pump.getId(), this);
            throw new IllegalStateException("PumpType not known: " + pump.getClass().getName());
        }

        webSocketService.broadcastPumpLayout(pumpService.getAllPumps());
    }

    public synchronized boolean isPumpRunning(long id) {
        return this.pumpingUpTasks.containsKey(id);
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
        automaticPumpBackTask = executor.scheduleAtFixedRate(() -> {
            List<Pump> allPumps = pumpService.getAllPumps();
            if (allPumps.stream().anyMatch(p -> pumpService.getPumpOccupation(p) != PumpService.PumpOccupation.NONE)) {
                return;
            }
            for (Pump pump : allPumps) {
                if (pump.isPumpedUp()) {
                    this.pumpBackOrUp(pump, Direction.BACKWARD);
                }
            }
        }, Instant.now().plusSeconds(60 * delay), Duration.ofMinutes(delay));
    }

    public synchronized void setReversePumpingSettings(ReversePumpingSettings.Full settings) {
        if (pumpService.getAllPumps().stream().anyMatch(p -> pumpService.getPumpOccupation(p) != PumpService.PumpOccupation.NONE)) {
            throw new IllegalStateException("Pumps occupied!");
        }

        optionsRepository.setOption("RPSEnable", Boolean.valueOf(settings.isEnable()).toString());
        if (settings.isEnable()) {
            ReversePumpingSettings.Details details = settings.getSettings();
            optionsRepository.setOption("RPSOvershoot", Integer.valueOf(details.getOvershoot()).toString());
            optionsRepository.setOption("RPSAutoPumpBackTimer", Integer.valueOf(details.getAutoPumpBackTimer()).toString());
            ReversePumpingSettings.VoltageDirectorPin vdPin = details.getDirectorPin();
            if (pumpService.findByBcmPin(vdPin.getBcmPin()).isPresent()) {
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

    public boolean isGpioInUseAdVdPin(int bcmPin) {
        if (this.reversePumpSettings == null || !this.reversePumpSettings.isEnable()) {
            return false;
        }
        return this.reversePumpSettings.getSettings().getDirectorPin().getBcmPin() == bcmPin;
    }

    private class DcMotorTask implements Runnable {
        DcPump dcPump;
        CountDownLatch cdl;
        Direction direction;

        DcMotorTask(DcPump dcPump, Direction direction) {
            this.dcPump = dcPump;
            this.direction = direction;
            this.cdl = new CountDownLatch(1);
        }

        @Override
        public void run() {
            dcPump.setPumpedUp(direction == Direction.FORWARD);
            try {
                cdl.await();
                PumpUpService.this.cancelPumpUp(dcPump);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class StepperMotorTask implements Runnable {
        StepperPump stepperPump;
        CountDownLatch cdl;
        Direction direction;
        double multiplier;

        StepperMotorTask(StepperPump stepperPump, Direction direction, double multiplier) {
            this.stepperPump = stepperPump;
            this.direction = direction;
            this.cdl = new CountDownLatch(1);
            this.multiplier = multiplier;
        }

        @Override
        public void run() {
            AcceleratingStepper driver = stepperPump.getMotorDriver();
            long nrSteps = (long) (multiplier * stepperPump.getStepsPerCl() * stepperPump.getTubeCapacityInMl());
            if (direction == Direction.BACKWARD) {
                nrSteps = -nrSteps;
            }
            driver.move(nrSteps);
            driver.runToPosition();
            driver.setEnable(false);
            stepperPump.setPumpedUp(direction == Direction.FORWARD);
            try {
                cdl.await();
                PumpUpService.this.cancelPumpUp(stepperPump);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
