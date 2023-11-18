package net.alex9849.cocktailpi.payload.dto.gpio;

import lombok.*;
import net.alex9849.cocktailpi.model.gpio.PinResource;

public class PinResourceDto {
    private interface Id { long getId(); }
    private interface Name { String getName(); }
    private interface Type { PinResource.Type getType(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode
        public static class Detailed implements Id, Type, Name {
            long id;
            PinResource.Type type;
            String name;

            public Detailed(PinResource pinResource) {
                this.id = pinResource.getId();
                this.type = pinResource.getType();
                this.name = pinResource.getName();
            }
        }

    }
}
