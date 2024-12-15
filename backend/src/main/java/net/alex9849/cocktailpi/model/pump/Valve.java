package net.alex9849.cocktailpi.model.pump;

import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailpi.model.LoadCell;
import net.alex9849.cocktailpi.service.LoadCellService;
import net.alex9849.cocktailpi.utils.SpringUtility;
import net.alex9849.motorlib.pin.IOutputPin;
import net.alex9849.motorlib.pin.PinState;

@DiscriminatorValue("valve")
public class Valve extends OnOffPump {
    private ValveDriver motorDriver;
    @Getter @Setter
    private long timePerClInMs;

    public Valve() {
        this.timePerClInMs = 100;
    }

    @Override
    public boolean isCanControlDirection() {
        return false;
    }

    public Double getTubeCapacityInMl() {
        return 3d;
    }

    public void setTubeCapacityInMl(Double tubeCapacityInMl) {
        //Ignore
    }

    @Override
    public ValveDriver getMotorDriver() {
        if(!isCanPump()) {
            throw new IllegalStateException("Motor not ready for pumping!");
        }
        if (motorDriver == null) {
            IOutputPin runPin = getPin().getOutputPin();
            motorDriver = new ValveDriver(runPin, isPowerStateHigh() ? PinState.HIGH : PinState.LOW);
        }
        return motorDriver;
    }

    @Override
    public void shutdownDriver() {
        if(this.motorDriver != null) {
            this.motorDriver.shutdown();
            this.motorDriver = null;
        }
    }

    public LoadCell getLoadCell() {
        LoadCellService lcService = SpringUtility.getBean(LoadCellService.class);
        return lcService.getLoadCell();

    }

    public boolean isCanPumpUp() {
        return this.isCanPump();
    }

    @Override
    public boolean isCanPump() {
        return isHwPinsCompleted()
                && getLoadCell() != null
                && getLoadCell().isCalibrated();
    }

    @Override
    protected boolean isCalibrationCompleted() {
        return isCanPump();
    }
}
