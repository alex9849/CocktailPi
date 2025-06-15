package net.alex9849.cocktailpi.model.gpio;

import com.pi4j.io.gpio.digital.PullResistance;
import net.alex9849.cocktailpi.service.GpioService;
import net.alex9849.cocktailpi.utils.SpringUtility;
import net.alex9849.motorlib.pin.IInputPin;
import net.alex9849.motorlib.pin.IOutputPin;

import java.util.Optional;

public abstract class Pin {
    private final int nr;
    private PinResource resource;
    private boolean resourceValid;
    private final GpioBoard board;
    private final String displayName;

    public Pin(GpioBoard board, int nr, String displayName) {
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

    public abstract IOutputPin getOutputPin();

    public abstract IInputPin getInputPin(PullResistance pull);

    public GpioBoard getGpioBoard() {
        return board;
    }

    public long getBoardId() {
        return getGpioBoard().getId();
    }

    public String getDisplayName() {
        return displayName;
    }
}
