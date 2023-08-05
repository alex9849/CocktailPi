package net.alex9849.cocktailmaker.service.pumps.cocktailfactory.productionstepworker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractProductionStepWorker {
    private final List<Consumer<StepProgress>> subscribers = new ArrayList<>();
    private boolean finished = false;
    private boolean started = false;
    private boolean cancelled = false;
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
        if(this.finished || this.cancelled) {
            throw new IllegalStateException("ProductionStepWorker has been canceled or finished!");
        }
        this.started = true;
    }

    public boolean cancel() {
        if(this.finished || this.cancelled) {
            return false;
        }
        this.cancelled = true;
        return true;
    }

    public abstract StepProgress getProgress();

    protected void setFinished() {
        if(this.finished || this.cancelled) {
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

    public boolean isCancelled() {
        return cancelled;
    }

    public void setOnFinishCallback(Runnable runnable) {
        this.onFinish = runnable;
    }
}
