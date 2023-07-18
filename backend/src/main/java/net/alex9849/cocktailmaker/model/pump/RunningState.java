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

    long mlPumped;
    long stepsMade;
    long startTime;
    Long stopTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunningState that = (RunningState) o;
        return finished == that.finished && running == that.running && inPumpUp == that.inPumpUp && isForward == that.isForward && percentage == that.percentage && mlPumped == that.mlPumped && stepsMade == that.stepsMade && startTime == that.startTime && Objects.equals(stopTime, that.stopTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(finished, running, inPumpUp, isForward, percentage, mlPumped, stepsMade, startTime, stopTime);
    }
}
