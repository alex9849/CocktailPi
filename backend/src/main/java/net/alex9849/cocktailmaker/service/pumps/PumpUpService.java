package net.alex9849.cocktailmaker.service.pumps;

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

    private final ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
    private ReversePumpingSettings.Full reversePumpSettings;
    private ScheduledFuture<?> automaticPumpBackTask;
    private final Map<Long, PumpTask> pumpingTasks = new HashMap<>();
    private Direction direction = Direction.FORWARD;

    public void postConstruct() {
        this.reversePumpSettings = this.getReversePumpingSettings();
        this.reschedulePumpBack();
    }

    public synchronized boolean isPumpPumpingUp(Pump pump) {
        return pumpingTasks.containsKey(pump.getId()) && pumpingTasks.get(pump.getId()).isPumpUp();
    }

    public synchronized boolean isPumpRunning(long id) {
        return this.pumpingTasks.containsKey(id);
    }

    public synchronized void cancelPumpUp(Pump pump) {
        PumpTask pumpTask = this.pumpingTasks.remove(pump.getId());
        if (pumpTask != null) {
            pumpTask.cancel();

            pumpLockService.releasePumpLock(pump.getId(), this);
            pumpService.updatePump(pump);
        }
    }

    public synchronized void runPumpOrPerformPumpUp(Pump pump, Direction direction, boolean isPumpUp) {
        if (!pumpLockService.testAndAcquirePumpLock(pump.getId(), this)) {
            throw new IllegalArgumentException("Pump is currently occupied!");
        }

        if (direction != this.direction && !this.pumpingTasks.isEmpty()) {
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
            dcMotor.setRunning(true);
            ScheduledFuture<?> stopTask;
            if(isPumpUp) {
                long duration = (long) (dcPump.getTubeCapacityInMl() * dcPump.getTimePerClInMs() * overshootMultiplier);
                stopTask = executor.scheduleWithFixedDelay(task, Duration.ofMillis(duration));
            } else {
                stopTask = executor.schedule(() -> {}, Instant.now());
            }

            this.pumpingTasks.put(pump.getId(), new PumpTask(stopTask, dcMotor, isPumpUp));
            task.cdl.countDown();

        } else if (pump instanceof StepperPump) {
            StepperPump stepperPump = (StepperPump) pump;
            long mlToPump = (long) (overshootMultiplier * stepperPump.getTubeCapacityInMl());
            if(!isPumpUp) {
                mlToPump = Long.MAX_VALUE;
            }
            StepperMotorTask task = new StepperMotorTask(stepperPump, this.direction, mlToPump);
            ScheduledFuture<?> stopTask = executor.scheduleWithFixedDelay(task, Duration.ZERO);
            this.pumpingTasks.put(pump.getId(), new PumpTask(stopTask, stepperPump.getMotorDriver(), isPumpUp));
            task.cdl.countDown();

        } else {
            pumpLockService.releasePumpLock(pump.getId(), this);
            throw new IllegalStateException("PumpType not known: " + pump.getClass().getName());
        }

        webSocketService.broadcastPumpLayout(pumpService.getAllPumps());
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
                    this.runPumpOrPerformPumpUp(pump, Direction.BACKWARD, true);
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

    private static class PumpTask {
        private final ScheduledFuture<?> future;
        private final IMotor motor;
        private final boolean isPumpUp;

        public PumpTask(ScheduledFuture<?> future, IMotor motor, boolean isPumpUp) {
            this.future = future;
            this.motor = motor;
            this.isPumpUp = isPumpUp;
        }

        public void cancel() {
            future.cancel(true);
            motor.shutdown();
        }

        public boolean isPumpUp() {
            return isPumpUp;
        }
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
        long mlToPump;

        /**
         *
         * @param mlToPump Long.MAX_VALUE == unlimited
         */
        StepperMotorTask(StepperPump stepperPump, Direction direction, long mlToPump) {
            this.stepperPump = stepperPump;
            this.direction = direction;
            this.cdl = new CountDownLatch(1);
            this.mlToPump = mlToPump;
        }

        @Override
        public void run() {
            AcceleratingStepper driver = stepperPump.getMotorDriver();
            long nrSteps = mlToPump * stepperPump.getStepsPerCl();
            if(mlToPump == Long.MAX_VALUE) {
                //Pick a very large number
                nrSteps = 10000000000L;
            } else {
                nrSteps = mlToPump * stepperPump.getStepsPerCl();
            }
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
