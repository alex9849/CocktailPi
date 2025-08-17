package net.alex9849.cocktailpi.model.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter @Service @NoArgsConstructor
public class ImportConfirmRequest {
    public enum DuplicateMode {
        OVERWRITE("overwrite"),
        SKIP("skip"),
        KEEP_BOTH("keep_both");

        private final String value;

        DuplicateMode(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @JsonCreator
        public static DuplicateMode fromValue(String value) {
            for (DuplicateMode mode : DuplicateMode.values()) {
                if (mode.value.equalsIgnoreCase(value)) {
                    return mode;
                }
            }
            throw new IllegalArgumentException("Unknown duplicate mode: " + value);
        }
    }

    private boolean importAllRecipes;
    private List<Long> importRecipeIds;
    private boolean importAllCollections;
    private List<Long> importCollectionIds;
    private boolean importAllGlasses;
    private boolean importAllCategories;
    private DuplicateMode duplicateMode;
}
