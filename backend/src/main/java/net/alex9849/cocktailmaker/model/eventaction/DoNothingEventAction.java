package net.alex9849.cocktailmaker.model.eventaction;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("DoNothing")
public class DoNothingEventAction extends EventAction {

    @Override
    public void trigger(RunningAction runningAction) {

    }

    @Override
    public String getDescription() {
        return "Do nothing";
    }
}
