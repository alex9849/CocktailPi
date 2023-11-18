package net.alex9849.cocktailpi.payload.dto.system.settings;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.alex9849.cocktailpi.model.system.settings.DefaultFilterSettings;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultFilterDto {

    private interface IEnable { boolean isEnable(); }
    private interface IFilter { Duplex.Detailed.Filter getFilter(); }
    private interface IFabricable { @NotNull String getFabricable(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Duplex {

        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements IEnable, IFilter {
            boolean enable;
            Filter filter;

            public Detailed() {}

            public Detailed(DefaultFilterSettings dfs) {
                this.enable = dfs.isEnable();
                if(dfs.getFilter() != null) {
                    this.filter = new Filter(dfs.getFilter());
                }
            }

            @Getter @Setter @EqualsAndHashCode
            public static class Filter implements IFabricable {
                String fabricable;

                public Filter() {}

                public Filter(DefaultFilterSettings.Filter filter) {
                    switch (filter.getFabricable()) {
                        case AUTOMATICALLY -> this.fabricable = "auto";
                        case IN_BAR -> this.fabricable = "manual";
                        case ALL -> this.fabricable = "";
                    }
                }
            }

        }

    }

}
