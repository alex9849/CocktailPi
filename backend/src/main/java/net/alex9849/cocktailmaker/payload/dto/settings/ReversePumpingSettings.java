package net.alex9849.cocktailmaker.payload.dto.settings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReversePumpingSettings {
    private interface Enable { boolean isEnable(); }
    private interface Settings { @Valid Details getSettings(); }
    private interface DirectorPin { @NotNull @Valid VoltageDirectorPin getDirectorPin(); }
    private interface BcmPin { @NotNull @Min(0) @Max(31) int getBcmPin(); }
    private interface Overshoot { @Min(0) @Max(200) int getOvershoot(); }
    private interface AutoPumpBackTimer { @Min(0) @Max(60) int getAutoPumpBackTimer(); }

    @Getter @Setter @EqualsAndHashCode
    public static class Full implements Enable, Settings {
        boolean enable;
        Details settings;
    }

    @Getter @Setter @EqualsAndHashCode
    public static class Details implements DirectorPin, Overshoot, AutoPumpBackTimer {
        VoltageDirectorPin directorPin;
        int overshoot;
        int autoPumpBackTimer;
    }

    @Getter @Setter @EqualsAndHashCode
    public static class VoltageDirectorPin implements BcmPin {
        int bcmPin;
    }
}
