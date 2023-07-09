package net.alex9849.cocktailmaker.model.pump;

import net.alex9849.cocktailmaker.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.repository.IngredientRepository;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import net.alex9849.motorlib.IMotor;

import java.util.Objects;

public abstract class Pump {
    private long id;
    private Double tubeCapacityInMl;
    private int fillingLevelInMl;
    private Long currentIngredientId;
    private boolean isPumpedUp;
    private AutomatedIngredient currentIngredient;
    private boolean isEnabled;
    private String name;

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
            IngredientRepository pRepository = SpringUtility.getBean(IngredientRepository.class);
            Ingredient ingredient = pRepository.findById(currentIngredientId).orElse(null);

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

    public abstract IMotor getMotorDriver();

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract boolean isCanPump();

    public boolean isCompleted() {
        return this.isCanPump() && this.tubeCapacityInMl != null && this.name != null;
    }
}
