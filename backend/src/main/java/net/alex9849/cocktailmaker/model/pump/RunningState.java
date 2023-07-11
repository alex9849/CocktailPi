package net.alex9849.cocktailmaker.model.pump;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RunningState {
    boolean running;
    boolean inPumpUp;
    boolean isForward;
    int percentage;


}
