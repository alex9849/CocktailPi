package net.alex9849.cocktailmaker.payload.dto.settings;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReversePumpingSettings {
    private interface Enable { boolean isEnable(); }
    private interface Settings { @Valid Details getSettings(); }
    private interface DirectorPins { @NotNull @Size(min = 2, max = 2) List<@Valid VoltageDirectorPin> getDirectorPins(); }
    private interface BcmPin { @NotNull @Min(0) @Max(31) int getBcmPin(); }
    private interface IsForwardStateHigh { boolean isForwardStateHigh(); }
    private interface Overshoot { @Min(0) @Max(200) int getOvershoot(); }
    private interface AutoPumpBackTimer { @Min(0) @Max(60) int getAutoPumpBackTimer(); }

    @Getter @Setter @EqualsAndHashCode
    public static class Full implements Enable, Settings {
        boolean enable;
        Details settings;
    }

    @Getter @Setter @EqualsAndHashCode
    public static class Details implements DirectorPins, Overshoot, AutoPumpBackTimer {
        List<VoltageDirectorPin> directorPins;
        int overshoot;
        int autoPumpBackTimer;
    }

    @Getter @Setter @EqualsAndHashCode
    public static class VoltageDirectorPin implements BcmPin, IsForwardStateHigh {
        int bcmPin;
        boolean forwardStateHigh;
    }
}
