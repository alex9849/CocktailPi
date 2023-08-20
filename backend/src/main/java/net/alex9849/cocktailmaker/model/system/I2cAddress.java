package net.alex9849.cocktailmaker.model.system;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailmaker.model.gpio.PinResource;
import net.alex9849.cocktailmaker.service.GpioService;
import net.alex9849.cocktailmaker.utils.SpringUtility;

import java.util.Optional;

public class I2cAddress {
    private int address;
    private PinResource resource;
    private boolean resourceValid;

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public PinResource getResource() {
        if(!resourceValid) {
            GpioService service = SpringUtility.getBean(GpioService.class);
            Optional<PinResource> oResource = service.getPinResourceByI2CAddress(address);
            resource = oResource.orElse(null);
            resourceValid = true;
        }
        return resource;
    }
}
