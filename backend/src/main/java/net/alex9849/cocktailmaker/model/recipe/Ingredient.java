package net.alex9849.cocktailmaker.model.recipe;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public abstract class Ingredient extends IngredientGroup {
    private int alcoholContent;
    private boolean inBar;

    public int getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(int alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    public abstract Unit getUnit();

    public boolean isInBar() {
        return inBar;
    }

    public void setInBar(boolean inBar) {
        this.inBar = inBar;
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
