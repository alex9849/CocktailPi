package net.alex9849.cocktailpi.model.system;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GpioStatus {
    private int pinsUsed;
    private int pinsAvailable;
    private int boardsAvailable;


}
