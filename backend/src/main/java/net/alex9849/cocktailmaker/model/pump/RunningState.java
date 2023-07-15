package net.alex9849.cocktailmaker.model.pump;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class RunningState {
    boolean finished;
    boolean running;
    boolean inPumpUp;
    boolean isForward;
    int percentage;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunningState that = (RunningState) o;
        return running == that.running && inPumpUp == that.inPumpUp && isForward == that.isForward && percentage == that.percentage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(running, inPumpUp, isForward, percentage);
    }
}
