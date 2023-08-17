package net.alex9849.cocktailmaker.model.gpio;

import net.alex9849.cocktailmaker.repository.GpioRepository;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import net.alex9849.motorlib.pin.IOutputPin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class GpioBoard {
    private long id;
    private String name;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract int getMinPin();

    public abstract int getMaxPin();

    public Pin getPin(int pin) {
        if(pin < getMinPin() || pin > getMaxPin()) {
            throw new IllegalArgumentException("Pin out of range! Requested pin: " + pin + ", Pin range: " + getMinPin() + " - " + getMaxPin());
        }
        return getPinUnchecked(pin);
    }

    protected abstract Pin getPinUnchecked(int pin);

    public List<Pin> getPins() {
        List<Pin> pinList = new ArrayList<>();
        for(int i = getMinPin(); i <= getMaxPin(); i++) {
            pinList.add(getPinUnchecked(i));
        }
        return pinList;
    }

    public abstract class Pin {
        private final int nr;
        private PinResource resource;
        private boolean resourceValid;

        public Pin(int nr) {
            this.nr = nr;
            this.resourceValid = false;
        }

        public int getPinNr() {
            return nr;
        }

        public PinResource getResource() {
            if(!resourceValid) {
                GpioRepository repo = SpringUtility.getBean(GpioRepository.class);
                Optional<PinResource> oResource = repo.getPinResourceByBoardIdAndPin(getBoardId(), getPinNr());
                resource = oResource.orElse(null);
                resourceValid = true;
            }
            return resource;
        }

        public abstract IOutputPin getOutputPin();

        public abstract GpioBoard getGpioBoard();

        public long getBoardId() {
            return getGpioBoard().getId();
        }
    }
}
