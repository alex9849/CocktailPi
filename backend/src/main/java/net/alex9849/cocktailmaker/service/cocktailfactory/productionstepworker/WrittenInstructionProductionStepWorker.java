package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

public class WrittenInstructionProductionStepWorker extends AbstractProductionStepWorker
        implements ManualFinishable {
    private final String message;

    public WrittenInstructionProductionStepWorker(String message) {
        this.message = message;
    }

    @Override
    public void start() {
        super.start();
        this.notifySubscribers();
    }

    @Override
    public void cancel() {}

    public void continueProduction() {
        if(!this.isStarted()) {
            return;
        }
        this.setFinished();
    }

    @Override
    public WrittenInstructionStepProgress getProgress() {
        WrittenInstructionStepProgress stepProgress = new WrittenInstructionStepProgress();
        stepProgress.setMessage(this.message);
        stepProgress.setPercentCompleted(0);
        stepProgress.setFinished(this.isFinished());
        return stepProgress;
    }
}
