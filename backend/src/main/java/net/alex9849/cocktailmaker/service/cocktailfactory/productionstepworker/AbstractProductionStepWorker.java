package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractProductionStepWorker {
    private final List<Consumer<StepProgress>> subscribers = new ArrayList<>();

    public AbstractProductionStepWorker subscribeToProgress(Consumer<StepProgress> progressInPercentConsumer) {
        this.subscribers.add(progressInPercentConsumer);
        return this;
    }

    protected void notifySubscribers() {
        for(Consumer<StepProgress> currConsumer : this.subscribers) {
            currConsumer.accept(getProgress());
        }
    }

    public abstract void start();

    public abstract void cancel();

    public abstract Mode getMode();

    protected abstract StepProgress getProgress();

    public abstract boolean isFinished();


    enum Mode {
        MANUAL, AUTOMATIC;
    }
}
