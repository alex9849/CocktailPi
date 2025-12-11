package net.alex9849.cocktailpi.model.pump;

import net.alex9849.cocktailpi.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.service.IngredientService;
import net.alex9849.cocktailpi.service.pumps.PumpMaintenanceService;
import net.alex9849.cocktailpi.utils.SpringUtility;
import net.alex9849.motorlib.motor.IMotor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Pump {
    protected static final Map<Long, IMotor> motorDrivers = new HashMap<>();

    private long id;
    private Double tubeCapacityInMl;
    private int fillingLevelInMl;
    private Long currentIngredientId;
    private boolean isPumpedUp;
    private AutomatedIngredient currentIngredient;
    private String name;
    private int powerConsumption;

    public int getPowerConsumption() {
        return powerConsumption;
    }

    public void setPowerConsumption(int powerConsumption) {
        this.powerConsumption = powerConsumption;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getTubeCapacityInMl() {
        return tubeCapacityInMl;
    }

    public void setTubeCapacityInMl(Double tubeCapacityInMl) {
        this.tubeCapacityInMl = tubeCapacityInMl;
    }

    public AutomatedIngredient getCurrentIngredient() {
        if(currentIngredientId != null && currentIngredient == null) {
            IngredientService ingredientService = SpringUtility.getBean(IngredientService.class);
            Ingredient ingredient = ingredientService.getIngredient(currentIngredientId);

            if(!(ingredient instanceof AutomatedIngredient)) {
                currentIngredientId = null;
                return null;
            }
            currentIngredient = (AutomatedIngredient) ingredient;
        }
        return currentIngredient;
    }

    public Long getCurrentIngredientId() {
        return currentIngredientId;
    }

    public boolean isPumpedUp() {
        return isPumpedUp;
    }

    public boolean isCanControlDirection() {
        PumpMaintenanceService service = SpringUtility.getBean(PumpMaintenanceService.class);
        return service.getReversePumpingSettings().isEnable();
    }

    public void setPumpedUp(boolean pumpedUp) {
        isPumpedUp = pumpedUp;
    }

    public void setCurrentIngredient(AutomatedIngredient currentIngredient) {
        this.currentIngredient = currentIngredient;
        this.currentIngredientId = (currentIngredient != null)? currentIngredient.getId() : null;
    }

    public int getFillingLevelInMl() {
        return fillingLevelInMl;
    }

    public void setFillingLevelInMl(int fillingLevelInMl) {
        this.fillingLevelInMl = fillingLevelInMl;
    }

    public void setCurrentIngredientId(Long currentIngredientId) {
        if(!Objects.equals(this.currentIngredientId, currentIngredientId)) {
            this.currentIngredient = null;
        }
        this.currentIngredientId = currentIngredientId;
    }

    public IMotor getMotorDriver() {
        synchronized (motorDrivers) {
            if(!isCanPump()) {
                throw new IllegalStateException("Motor not ready for pumping!");
            }
            return motorDrivers.computeIfAbsent(getId(), x -> genMotorDriver());
        }
    }

    protected abstract IMotor genMotorDriver();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrintName () {
        if (getName() == null || getName().isEmpty()) {
            return "Pump #" + this.id;
        } else {
            return getName();
        }
    }

    public abstract boolean isCanPump();

    public void shutdownDriver(boolean createNonExistent) {
        synchronized (motorDrivers) {
            if(createNonExistent && isCanPump()) {
                getMotorDriver();
            }
            if(motorDrivers.containsKey(getId())) {
                motorDrivers.get(getId()).shutdown();
                motorDrivers.remove(getId());
            }
        }
    }

    public abstract boolean equalDriverProperties(Pump other);

    public boolean isCanPumpUp() {
        return this.isCanPump() && this.tubeCapacityInMl != null;
    }

    public boolean isCompleted() {
        return this.isCanPumpUp();
    }

    protected abstract boolean isHwPinsCompleted();

    protected abstract boolean isCalibrationCompleted();

    public SetupStage getSetupStage() {
        boolean handleComplete = getName() != null;
        boolean isHwPinsComplete = isHwPinsCompleted();
        boolean isCalibrationComplete = isCalibrationCompleted();
        SetupStage stage = SetupStage.HANDLE;
        if(handleComplete) {
            stage = SetupStage.HW_PINS;
        }
        if(isHwPinsComplete) {
            stage = SetupStage.CALIBRATE;
        }
        if(isHwPinsComplete && isCalibrationComplete) {
            stage = SetupStage.STATE;
        }
        return stage;
    }

    public enum SetupStage {
        HANDLE(0), HW_PINS(1), CALIBRATE(2), STATE(3);
        public int level;
        SetupStage(int level) {
            this.level = level;
        }
    }
}
