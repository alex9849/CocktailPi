package net.alex9849.cocktailpi.model.pump;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailpi.service.LoadCellService;
import net.alex9849.cocktailpi.utils.SpringUtility;
import net.alex9849.motorlib.motor.IMotor;

@DiscriminatorValue("valve")
public class Valve extends OnOffPump {

    @Override
    public IMotor getMotorDriver() {
        return null;
    }

    @Override
    protected void resetDriver() {

    }

    @Override
    public boolean isCanPump() {
        LoadCellService lcService = SpringUtility.getBean(LoadCellService.class);
        return isHwPinsCompleted() && lcService.getLoadCell() != null;
    }

    @Override
    protected boolean isCalibrationCompleted() {
        return true;
    }
}
