package net.alex9849.cocktailpi.payload.dto.pump;

import lombok.*;
import net.alex9849.cocktailpi.model.pump.Valve;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValveDto {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Create extends OnOffPumpDto.Request.Create  {}
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends OnOffPumpDto.Response.Detailed {

            protected Detailed(Valve valve) {
                super(valve);
            }

            @Override
            public String getType() {
                return "valve";
            }
        }
    }
}
