package net.alex9849.cocktailpi.model.pump;

import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailpi.model.LoadCell;
import net.alex9849.cocktailpi.service.LoadCellService;
import net.alex9849.cocktailpi.utils.SpringUtility;
import net.alex9849.motorlib.motor.IMotor;
import net.alex9849.motorlib.pin.IOutputPin;
import net.alex9849.motorlib.pin.PinState;

@DiscriminatorValue("valve")
public class Valve extends OnOffPump {
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
        return (ValveDriver) super.getMotorDriver();
    }

    @Override
    protected IMotor genMotorDriver() {
        return new ValveDriver(getPin().getOutputPin(), isPowerStateHigh() ? PinState.HIGH : PinState.LOW);
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
