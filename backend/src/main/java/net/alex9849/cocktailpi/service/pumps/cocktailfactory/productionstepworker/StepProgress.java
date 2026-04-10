package net.alex9849.cocktailpi.service.pumps.cocktailfactory.productionstepworker;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StepProgress {
    private boolean finished;
    private int percentCompleted;
}
