package net.alex9849.cocktailpi.payload.dto.system;

import lombok.*;
import net.alex9849.cocktailpi.model.gpio.PinResource;
import net.alex9849.cocktailpi.model.system.I2cAddress;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class I2cAddressDto {

    @Getter @Setter @EqualsAndHashCode
    public static class Response {
        int address;
        PinResource resource;
        boolean inUse;

        public Response(I2cAddress address) {
            this.address = address.getAddress();
            if(address.getResource() != null) {
                this.resource = address.getResource();
            }
            this.inUse = this.resource != null;
        }

    }
}
