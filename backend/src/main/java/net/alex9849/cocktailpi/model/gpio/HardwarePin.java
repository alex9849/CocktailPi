package net.alex9849.cocktailpi.model.gpio;

import com.pi4j.io.gpio.digital.PullResistance;
import lombok.Getter;
import net.alex9849.cocktailpi.service.GpioService;
import net.alex9849.cocktailpi.utils.SpringUtility;

import java.util.Objects;
import java.util.Optional;

public abstract class HardwarePin {
    private final int nr;
    private PinResource resource;
    private boolean resourceValid;
    private final GpioBoard board;
    @Getter
    private final String displayName;

    public HardwarePin(GpioBoard board, int nr, String displayName) {
        this.nr = nr;
        this.resourceValid = false;
        this.board = board;
        this.displayName = displayName;
    }

    public int getPinNr() {
        return nr;
    }

    public PinResource getResource() {
        if(!resourceValid) {
            GpioService service = SpringUtility.getBean(GpioService.class);
            Optional<PinResource> oResource = service.getPinResourceByBoardIdAndPin(getBoardId(), getPinNr());
            resource = oResource.orElse(null);
            resourceValid = true;
        }
        return resource;
    }

    public abstract OutputPin getOutputPin();

    public abstract InputPin getInputPin(PullResistance pull);

    public GpioBoard getGpioBoard() {
        return board;
    }

    public long getBoardId() {
        return getGpioBoard().getId();
    }

    public boolean isExceptional() {
        return this.getGpioBoard().isExceptional();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HardwarePin that)) return false;
        return nr == that.nr && Objects.equals(board, that.board);
    }

}
