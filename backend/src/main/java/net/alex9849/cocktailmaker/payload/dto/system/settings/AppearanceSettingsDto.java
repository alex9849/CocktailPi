package net.alex9849.cocktailmaker.payload.dto.system.settings;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.alex9849.cocktailmaker.model.system.settings.Language;

public class AppearanceSettingsDto {

    private interface ILanguage { @NotNull Language getLanguage(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Duplex {
        @NoArgsConstructor()
        @Getter @Setter
        public static class Detailed implements ILanguage {
            Language language;

        }

    }
}
