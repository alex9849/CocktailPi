package net.alex9849.cocktailmaker.service.pumps.cocktailfactory.productionstepworker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractProductionStepWorker {
    private final List<Consumer<StepProgress>> subscribers = new ArrayList<>();
    private boolean finished = false;
    private boolean started = false;
    private Runnable onFinish = null;

    public AbstractProductionStepWorker subscribeToProgress(Consumer<StepProgress> progressInPercentConsumer) {
        this.subscribers.add(progressInPercentConsumer);
        return this;
    }

    protected void notifySubscribers() {
        for(Consumer<StepProgress> currConsumer : this.subscribers) {
            currConsumer.accept(getProgress());
        }
    }

    public void start() {
        if(this.started) {
            throw new IllegalStateException("ProductionStepWorker has already been started!");
        }
        this.started = true;
    }

    public abstract void cancel();

    public abstract StepProgress getProgress();

    protected void setFinished() {
        if(this.finished) {
            return;
        }
        this.finished = true;
        if(this.onFinish != null) {
            this.onFinish.run();
        }
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void setOnFinishCallback(Runnable runnable) {
        this.onFinish = runnable;
    }

    enum Mode {
        MANUAL, AUTOMATIC;
    }
}
