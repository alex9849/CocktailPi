package net.alex9849.cocktailmaker.model.recipe.ingredient;

import com.fasterxml.jackson.annotation.JsonValue;
import net.alex9849.cocktailmaker.service.IngredientService;
import net.alex9849.cocktailmaker.utils.SpringUtility;

import java.util.Objects;

public abstract class Ingredient {
    private Long id;
    private String name;
    private Long parentGroupId;
    private IngredientGroup parentGroup;

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentGroupId(Long parentGroupId) {
        if(!Objects.equals(parentGroupId, id)) {
            this.parentGroup = null;
        }
        this.parentGroupId = parentGroupId;
    }

    public IngredientGroup getParentGroup() {
        if(parentGroupId != null && parentGroup == null) {
            IngredientService iService = SpringUtility.getBean(IngredientService.class);
            parentGroup = (IngredientGroup) iService.getIngredient(parentGroupId);
            parentGroupId = parentGroup.getId();
        }
        return parentGroup;
    }

    public void setParentGroup(IngredientGroup ingredientGroup) {
        parentGroup = ingredientGroup;
        if(ingredientGroup == null) {
            parentGroupId = null;
            return;
        }
        parentGroupId = parentGroup.getId();
    }

    public Long getParentGroupId() {
        return parentGroupId;
    }

    public abstract boolean isInBar();

    public abstract boolean isOnPump();

    public abstract Unit getUnit();

    public enum Unit {
        MILLILITER("ml"), GRAM("g"), TEASPOON("teaspoon(s)"),
        TABLESPOON("tablespoon(s)"), PIECE("piece(s)");
        private final String displayUnit;

        Unit(String displayUnit) {
            this.displayUnit = displayUnit;
        }

        public static Unit findByDisplayUnit(String displayUnit) {
            for(Unit current : Unit.values()) {
                if(current.getDisplayUnit().equals(displayUnit)) {
                    return current;
                }
            }
            return null;
        }

        @JsonValue
        public String getDisplayUnit() {
            return displayUnit;
        }

    }
}
