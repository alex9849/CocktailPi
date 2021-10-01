package net.alex9849.cocktailmaker.model.recipe;

import com.fasterxml.jackson.annotation.JsonValue;

public abstract class Ingredient {
    private long id;
    private String name;
    private int alcoholContent;

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

    public int getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(int alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    public abstract Unit getUnit();

    public boolean isAddToVolume() {
        return true;
    }

    public boolean isScaleToVolume() {
        return true;
    }

    public enum Unit {
        MILLILITER("ml"), GRAM("g"), TEASPOON("teaspoon(s)"),
        TABLESPOON("tablespoon(s)"), PIECE("piece(s)");
        private final String displayUnit;

        Unit(String displayUnit) {
            this.displayUnit = displayUnit;
        }

        @JsonValue
        public String getDisplayUnit() {
            return displayUnit;
        }

        public static Unit findByDisplayUnit(String displayUnit) {
            for(Unit current : Unit.values()) {
                if(current.getDisplayUnit().equals(displayUnit)) {
                    return current;
                }
            }
            return null;
        }

    }
}
