package net.alex9849.cocktailpi.model;

import com.pi4j.io.gpio.digital.PullResistance;
import lombok.Getter;
import net.alex9849.cocktailpi.model.gpio.HardwarePin;
import net.alex9849.cocktailpi.model.pump.LoadCellReader;
import net.alex9849.motorlib.sensor.HX711;

public class LoadCell {
    private static HX711 hx711;
    private static LoadCellReader reader;

    @Getter
    private HardwarePin clkHwPin;
    @Getter
    private HardwarePin dtHwPin;
    @Getter
    private long zeroForceValue;
    @Getter
    private Long referenceForceValue;
    @Getter
    private Long referenceForceValueWeight;


    private HX711 genHX711() {
        shutdown();
        if(!isCalibrateable()) {
            return null;
        }
        HX711 hx711 = new HX711(dtHwPin.getInputPin(PullResistance.OFF), clkHwPin.getOutputPin(), 128);
        hx711.calibrateEmpty(zeroForceValue);
        if(isCalibrated()) {
            hx711.calibrateWeighted(referenceForceValueWeight, referenceForceValue);
        }
        return hx711;
    }

    public synchronized HX711 getHX711() {
        if(hx711 == null) {
            hx711 = genHX711();
        }
        return hx711;
    }

    public synchronized LoadCellReader getLoadCellReader() {
        HX711 hx711 = getHX711();
        if(hx711 == null) {
            return null;
        }
        if (reader == null) {
            reader = new LoadCellReader(hx711, 7);
        }
        return reader;
    }

    public synchronized void shutdown() {
        if(hx711 != null) {
            hx711 = null;
        }
        if(reader != null) {
            try {
                reader.shutdownBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            reader = null;
        }
    }

    public boolean isCalibrateable() {
        return clkHwPin != null && dtHwPin != null;
    }

    public boolean isCalibrated() {
        return isCalibrateable() && referenceForceValueWeight != null;
    }

    public void setClkHwPin(HardwarePin clkHwPin) {
        this.clkHwPin = clkHwPin;
        hx711 = null;
    }

    public void setDtHwPin(HardwarePin dtHwPin) {
        this.dtHwPin = dtHwPin;
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
