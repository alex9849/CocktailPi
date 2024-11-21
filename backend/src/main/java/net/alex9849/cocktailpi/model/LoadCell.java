package net.alex9849.cocktailpi.model;

import com.pi4j.io.gpio.digital.PullResistance;
import lombok.Getter;
import net.alex9849.cocktailpi.model.gpio.Pin;
import net.alex9849.motorlib.sensor.HX711;

public class LoadCell {
    @Getter
    private Pin clkPin;
    @Getter
    private Pin dtPin;
    @Getter
    private long zeroForceValue;
    @Getter
    private Long referenceForceValue;
    @Getter
    private Long referenceForceValueWeight;
    private HX711 hx711;

    public HX711 getHX711() {
        if(hx711 == null) {
            if(!isCalibrateable()) {
                return null;
            }
            hx711 = new HX711(dtPin.getInputPin(PullResistance.OFF), clkPin.getOutputPin(), 128);
            hx711.calibrateEmpty(zeroForceValue);
            if(isCalibrated()) {
                hx711.calibrateWeighted(referenceForceValueWeight, referenceForceValue);
            }
        }
        return hx711;
    }

    public boolean isCalibrateable() {
        return clkPin != null && dtPin != null;
    }

    public boolean isCalibrated() {
        return isCalibrateable() && referenceForceValueWeight != null;
    }

    public void setClkPin(Pin clkPin) {
        this.clkPin = clkPin;
        hx711 = null;
    }

    public void setDtPin(Pin dtPin) {
        this.dtPin = dtPin;
        hx711 = null;
    }

    public void setZeroForceValue(long zeroForceValue) {
        this.zeroForceValue = zeroForceValue;
        hx711 = null;
    }

    public void setReferenceForceValue(Long referenceForceValue) {
        this.referenceForceValue = referenceForceValue;
        hx711 = null;
    }

    public void setReferenceForceValueWeight(Long referenceForceValueWeight) {
        this.referenceForceValueWeight = referenceForceValueWeight;
        hx711 = null;
    }

}
