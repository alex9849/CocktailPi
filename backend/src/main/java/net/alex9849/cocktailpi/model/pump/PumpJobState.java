package net.alex9849.cocktailpi.model.pump;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailpi.model.pump.motortasks.PumpTask;

import java.util.Objects;

@Getter @Setter
public class PumpJobState {
    Long lastJobId;
    RunningState runningState;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PumpJobState jobState = (PumpJobState) o;
        return Objects.equals(lastJobId, jobState.lastJobId) && Objects.equals(runningState, jobState.runningState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastJobId, runningState);
    }

    @Getter @Setter
    public static class RunningState {
        long jobId;
        boolean isRunInfinity;
        boolean isForward;
        int percentage;
        PumpTask.State state;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RunningState that = (RunningState) o;
            return jobId == that.jobId && isRunInfinity == that.isRunInfinity && isForward == that.isForward && percentage == that.percentage && state == that.state;
        }

        @Override
        public int hashCode() {
            return Objects.hash(jobId, isRunInfinity, isForward, percentage, state);
        }
    }
}

