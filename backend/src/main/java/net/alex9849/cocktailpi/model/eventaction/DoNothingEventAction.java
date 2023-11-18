package net.alex9849.cocktailpi.model.eventaction;


import jakarta.persistence.DiscriminatorValue;

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
