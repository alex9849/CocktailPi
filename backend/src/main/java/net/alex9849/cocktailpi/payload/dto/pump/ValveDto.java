package net.alex9849.cocktailpi.payload.dto.pump;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import net.alex9849.cocktailpi.model.pump.Valve;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValveDto {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter @Setter
        @EqualsAndHashCode(callSuper = true)
        @NoArgsConstructor(access = AccessLevel.PUBLIC)
        public static class Create extends OnOffPumpDto.Request.Create  {

            public Create(Valve pump) {
                super(pump);
            }

            @JsonIgnore
            public Double getTubeCapacityInMl() {
                return 1d;
            }

        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends OnOffPumpDto.Response.Detailed {
            private boolean loadCell;
            private boolean loadCellCalibrated;

            protected Detailed(Valve valve) {
                super(valve);
                loadCell = valve.getLoadCell() != null;
                loadCellCalibrated = loadCell && valve.getLoadCell().isCalibrated();
            }

            @JsonIgnore
            public Double getTubeCapacityInMl() {
                return 1d;
            }

            @Override
            public String getType() {
                return "valve";
            }
        }
    }
}
