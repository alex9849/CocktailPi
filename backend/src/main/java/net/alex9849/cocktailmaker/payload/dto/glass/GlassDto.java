package net.alex9849.cocktailmaker.payload.dto.glass;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.alex9849.cocktailmaker.model.Glass;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlassDto {
    private interface Id { long getId(); }
    private interface Name { @NotNull @jakarta.validation.constraints.Size(min = 1, max = 15) String getName(); }
    private interface Size { @NotNull @Min(1) @Max(5000) long getSize(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Duplex {

        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements Id, Name, GlassDto.Size {
            long id;
            String name;
            long size;

            public Detailed() {}
            public Detailed(Glass glass) {
                id = glass.getId();
                name = glass.getName();
                size = glass.getSize();
            }
        }

    }


}
