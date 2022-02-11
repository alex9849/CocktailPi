package net.alex9849.cocktailmaker.model.recipe;

import com.fasterxml.jackson.annotation.JsonValue;
import net.alex9849.cocktailmaker.service.IngredientService;
import net.alex9849.cocktailmaker.utils.SpringUtility;

public abstract class Ingredient {
    private long id;
    private String name;
    private Long parentGroupId;
    private String parentGroupName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentGroupName() {
        //Lazy load
        if(parentGroupId == null) {
            return null;
        }
        if(parentGroupName == null) {
            IngredientService iService = SpringUtility.getBean(IngredientService.class);
            Ingredient parent = iService.getIngredient(parentGroupId);
            if(parent == null) {
                parentGroupId = null;
                return null;
            }
            parentGroupName = parent.getName();
        }
        return parentGroupName;
    }

    public Long getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(Long parentGroupId) {
        this.parentGroupName = null;
        this.parentGroupId = parentGroupId;
    }

    public abstract boolean isInBar();

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
