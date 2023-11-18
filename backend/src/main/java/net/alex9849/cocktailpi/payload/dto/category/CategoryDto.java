package net.alex9849.cocktailpi.payload.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.beans.BeanUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryDto {
    private interface Id { long getId(); }
    private interface Name { @NotNull @Size(min = 1, max = 15) String getName(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        public static class Create implements Name {
            String name;

            public Create() {}

            public Create(Duplex.Detailed detailed) {
                BeanUtils.copyProperties(detailed, this);
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Duplex {
        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements Id, Name {
            long id;
            String name;

            public Detailed() {}

            public Detailed(net.alex9849.cocktailpi.model.Category category) {
                BeanUtils.copyProperties(category, this);
            }
        }
    }
}
