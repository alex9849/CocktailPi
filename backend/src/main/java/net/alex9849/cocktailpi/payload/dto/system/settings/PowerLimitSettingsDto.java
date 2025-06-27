package net.alex9849.cocktailpi.payload.dto.system.settings;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PowerLimitSettingsDto {
    private interface IEnable { boolean isEnable(); }
    private interface IPowerLimitLimit { @NotNull @Min(1) int getLimit(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Duplex {
        @NoArgsConstructor()
        @Getter
        @Setter
        public static class Detailed implements IEnable, IPowerLimitLimit {
            boolean enable;
            int limit;
        }

    }
}
