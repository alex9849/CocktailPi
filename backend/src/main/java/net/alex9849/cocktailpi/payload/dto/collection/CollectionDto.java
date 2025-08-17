package net.alex9849.cocktailpi.payload.dto.collection;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.alex9849.cocktailpi.model.Collection;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionDto {
    private interface Id { long getId(); }
    private interface Name { @NotNull @jakarta.validation.constraints.Size(min = 3, max = 20) String getName(); }
    private interface NormalName { String getNormalName(); }
    private interface Description { @NotNull @jakarta.validation.constraints.Size(max = 2000) String getDescription(); }
    private interface HasImage { boolean isHasImage(); }
    private interface Size { int getSize(); }
    private interface LastUpdate { Date getLastUpdate(); }
    private interface OwnerName { String getOwnerName(); }
    private interface OwnerId { long getOwnerId(); }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        public static class Create implements Name, Description {
            String name;
            String description;

            public Create () {}

            public Create(Response.Detailed collection) {
                BeanUtils.copyProperties(collection, this);
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements Id, Name, NormalName, Description, HasImage, CollectionDto.Size, LastUpdate,
                OwnerId, OwnerName {
            long id;
            String name;
            String normalName;
            String description;
            String ownerName;
            long ownerId;
            boolean hasImage;
            int size;
            Date lastUpdate;

            public Detailed () {}

            public Detailed(Collection collection) {
                BeanUtils.copyProperties(collection, this);
                normalName = collection.getNormalName();
                ownerName = collection.getOwner().getUsername();
                ownerId = collection.getOwnerId();
            }
        }
    }
}
