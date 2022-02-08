package net.alex9849.cocktailmaker.model.recipe;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("IngredientGroup")
public class IngredientGroup {
    private long id;
    private String name;
    private Long parentGroupId;

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

    public Long getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(Long parentGroupId) {
        this.parentGroupId = parentGroupId;
    }

    public Unit getUnit() {
        return Unit.MILLILITER;
    }

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
