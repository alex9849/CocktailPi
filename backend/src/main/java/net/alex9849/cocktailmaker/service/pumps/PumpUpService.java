package net.alex9849.cocktailmaker.service.pumps;

import net.alex9849.cocktailmaker.hardware.IGpioController;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.payload.dto.settings.ReversePumpingSettings;
import net.alex9849.cocktailmaker.repository.OptionsRepository;
import net.alex9849.cocktailmaker.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@Transactional
public class PumpUpService {
    @Autowired
    private PumpService pumpService;

    @Autowired
    private OptionsRepository optionsRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private IGpioController gpioController;

    private ScheduledFuture<?> automaticPumpBackTask;
    private ReversePumpingSettings.Full reversePumpSettings;
    private final Map<Long, ScheduledFuture<?>> pumpingUpPumpIdsToStopTask = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void postConstruct() {
        this.reversePumpSettings = this.getReversePumpingSettings();
        this.setReversePumpingDirection(false);
        this.updateReversePumpSettingsCountdown();
    }

    public synchronized boolean isPumpPumpingUp(Pump pump) {
        return this.pumpingUpPumpIdsToStopTask.containsKey(pump.getId());
    }

    public synchronized void cancelPumpUp(Pump pump) {
        ScheduledFuture<?> pumpUpFuture = this.pumpingUpPumpIdsToStopTask.remove(pump.getId());
        if (pumpUpFuture != null) {
            pump.setRunning(false);
            pumpUpFuture.cancel(false);
            pumpService.updatePump(pump);
        }
        if (this.pumpingUpPumpIdsToStopTask.isEmpty()) {
            this.setReversePumpingDirection(false);
        }
    }

    public synchronized void pumpBackOrUp(Pump pump, boolean pumpUp) {
        if ((pumpUp == this.isPumpDirectionReversed()) && !this.pumpingUpPumpIdsToStopTask.isEmpty()) {
            throw new IllegalArgumentException("A pump is currently pumping into the other direction!");
        }
        if (this.pumpingUpPumpIdsToStopTask.containsKey(pump.getId())) {
            throw new IllegalArgumentException("Pump is already pumping up/back!");
        }
        if (this.pumpService.getPumpOccupation(pump) != PumpService.PumpOccupation.NONE) {
            throw new IllegalArgumentException("Pump is currently occupied!");
        }
        //throws exception is pumpUp is false and reversePumpSettings not configured!
        this.setReversePumpingDirection(!pumpUp);
        int overShoot = 1;
        if (pumpUp) {
            this.updateReversePumpSettingsCountdown();
        } else {
            overShoot = reversePumpSettings.getSettings().getOvershoot();
        }
        double overShootMultiplier = 1 + (((double) overShoot) / 100);
        int runTime = (int) (pump.getConvertMlToRuntime(pump.getTubeCapacityInMl()) * overShootMultiplier);
        pump.setRunning(true);
        CountDownLatch cdl = new CountDownLatch(1);
        ScheduledFuture<?> stopTask = scheduler.schedule(() -> {
            pump.setPumpedUp(pumpUp);
            try {
                cdl.await();
                this.cancelPumpUp(pump);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, runTime, TimeUnit.MILLISECONDS);
        this.pumpingUpPumpIdsToStopTask.put(pump.getId(), stopTask);
        cdl.countDown();
        webSocketService.broadcastPumpLayout(pumpService.getAllPumps());
    }

    public synchronized boolean isPumpDirectionReversed() {
        if (this.reversePumpSettings == null || !this.reversePumpSettings.isEnable()) {
            return false;
        }
        ReversePumpingSettings.VoltageDirectorPin pin = this.reversePumpSettings.getSettings().getDirectorPin();
        IGpioPin gpioPin = gpioController.getGpioPin(pin.getBcmPin());
        return gpioPin.isHigh() != pin.isForwardStateHigh();
    }

    private synchronized void setReversePumpingDirection(boolean reverse) {
        if (reversePumpSettings == null || !reversePumpSettings.isEnable()) {
            if (reverse) {
                throw new IllegalStateException("Can't change pump direction! Reverse pump settings not defined!");
            } else {
                return;
            }
        }
        ReversePumpingSettings.VoltageDirectorPin pinConfig = reversePumpSettings.getSettings().getDirectorPin();
        IGpioPin gpioPin = gpioController.getGpioPin(pinConfig.getBcmPin());
        if (reverse != pinConfig.isForwardStateHigh()) {
            if (!gpioPin.isHigh()) {
                gpioPin.setHigh();
            }
        } else {
            if(gpioPin.isHigh()) {
                gpioPin.setLow();
            }
        }
    }

    public synchronized void updateReversePumpSettingsCountdown() {
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
        automaticPumpBackTask = scheduler.scheduleAtFixedRate(() -> {
            List<Pump> allPumps = pumpService.getAllPumps();
            if (allPumps.stream().anyMatch(p -> pumpService.getPumpOccupation(p) != PumpService.PumpOccupation.NONE)) {
                return;
            }
            for (Pump pump : allPumps) {
                if (pump.isPumpedUp()) {
                    this.pumpBackOrUp(pump, false);
                }
            }
        }, delay, delay, TimeUnit.MINUTES);
    }

    public synchronized void setReversePumpingSettings(ReversePumpingSettings.Full settings) {
        if(pumpService.getAllPumps().stream().anyMatch(p -> pumpService.getPumpOccupation(p) != PumpService.PumpOccupation.NONE)) {
            throw new IllegalStateException("Pumps occupied!");
        }

        optionsRepository.setOption("RPSEnable", Boolean.valueOf(settings.isEnable()).toString());
        if (settings.isEnable()) {
            ReversePumpingSettings.Details details = settings.getSettings();
            optionsRepository.setOption("RPSOvershoot", Integer.valueOf(details.getOvershoot()).toString());
            optionsRepository.setOption("RPSAutoPumpBackTimer", Integer.valueOf(details.getAutoPumpBackTimer()).toString());
            ReversePumpingSettings.VoltageDirectorPin vdPin = details.getDirectorPin();
            if(pumpService.findByBcmPin(vdPin.getBcmPin()).isPresent()) {
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
        this.setReversePumpingDirection(false);
        updateReversePumpSettingsCountdown();
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
        if(this.reversePumpSettings == null || !this.reversePumpSettings.isEnable()) {
            return false;
        }
        return this.reversePumpSettings.getSettings().getDirectorPin().getBcmPin() == bcmPin;
    }
}
