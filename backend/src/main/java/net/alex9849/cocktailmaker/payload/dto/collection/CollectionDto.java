package net.alex9849.cocktailmaker.payload.dto.collection;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.alex9849.cocktailmaker.model.Collection;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionDto {
    private interface Id { long getId(); }
    private interface Name { @NotNull @jakarta.validation.constraints.Size(min = 3, max = 20) String getName(); }
    private interface Description { @NotNull @jakarta.validation.constraints.Size(max = 2000) String getDescription(); }
    private interface HasImage { boolean isHasImage(); }
    private interface Size { int getSize(); }
    private interface LastUpdate { Date getLastUpdate(); }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        public static class Create implements Name, Description {
            String name;
            String description;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements Id, Name, Description, HasImage, CollectionDto.Size, LastUpdate {
            long id;
            String name;
            String description;
            boolean hasImage;
            int size;
            Date lastUpdate;

            public Detailed(Collection collection) {
                BeanUtils.copyProperties(collection, this);
            }
        }
    }
}
