package net.alex9849.cocktailmaker.model.pump;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class JobMetrics {
    long id;
    long mlPumped;
    long stepsMade;
    long startTime;
    Long stopTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobMetrics that = (JobMetrics) o;
        return id == that.id && mlPumped == that.mlPumped && stepsMade == that.stepsMade && startTime == that.startTime && Objects.equals(stopTime, that.stopTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mlPumped, stepsMade, startTime, stopTime);
    }
}
