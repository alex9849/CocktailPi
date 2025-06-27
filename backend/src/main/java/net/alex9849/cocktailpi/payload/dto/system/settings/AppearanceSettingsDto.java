package net.alex9849.cocktailpi.payload.dto.system.settings;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.alex9849.cocktailpi.model.system.settings.Language;

public class AppearanceSettingsDto {

    private interface ILanguage { @NotNull Language getLanguage(); }
    private interface IRecipePageSize { @NotNull @Min(8) @Max(200) int getRecipePageSize(); }
    private final static String hexColorPatten = "^#[A-Fa-f0-9]{6}$";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Duplex {
        @NoArgsConstructor()
        @Getter @Setter
        public static class Detailed implements ILanguage, IRecipePageSize {
            Language language;
            int recipePageSize;
            @NotNull
            Colors colors;
        }

        @NoArgsConstructor()
        @Getter @Setter
        public static class Colors {
            @NotNull
            NormalColors normal;
            @NotNull
            SvColors simpleView;

        }

        @NoArgsConstructor()
        @Getter @Setter
        public static class NormalColors {
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String header;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String sidebar;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String background;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String btnNavigationActive;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String btnPrimary;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String cardHeader;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String cardBody;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String cardItemGroup;
        }

        @NoArgsConstructor()
        @Getter @Setter
        public static class SvColors {
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String header;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String sidebar;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String background;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String btnNavigation;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String btnNavigationActive;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String btnPrimary;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String cocktailProgress;
            @NotNull
            @Pattern(regexp = hexColorPatten)
            String cardPrimary;
        }

    }
}
