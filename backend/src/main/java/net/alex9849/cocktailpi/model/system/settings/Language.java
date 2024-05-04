package net.alex9849.cocktailpi.model.system.settings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Language {
    en_US("English (US)", "English (US)"),
    de("German", "Deutsch"),
    da_DK("Danish", "Dansk");

    final String inEnglish;
    final String inNative;


    Language(String inEnglish, String inNative) {
        this.inEnglish = inEnglish;
        this.inNative = inNative;
    }

    @JsonCreator
    public static Language forValues(@JsonProperty("name") String name) {
        for (Language language : Language.values()) {
            if (language.name().equals(name)) {
                return language;
            }
        }
        return null;
    }
    @JsonProperty
    public String getName() {
        return this.name();
    }

    @JsonProperty
    public String getInEnglish() {
        return inEnglish;
    }

    @JsonProperty
    public String getInNative() {
        return inNative;
    }
}
