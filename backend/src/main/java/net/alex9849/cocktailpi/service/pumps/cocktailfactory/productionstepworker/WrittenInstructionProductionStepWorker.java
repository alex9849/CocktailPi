package net.alex9849.cocktailpi.service.pumps.cocktailfactory.productionstepworker;

import net.alex9849.cocktailpi.service.pumps.cocktailfactory.CocktailFactory;

public class WrittenInstructionProductionStepWorker extends AbstractProductionStepWorker
        implements ManualFinishable {
    private final String message;

    public WrittenInstructionProductionStepWorker(CocktailFactory cocktailFactory, String message) {
        super(cocktailFactory);
        this.message = message;
    }

    @Override
    public void start() {
        super.start();
        this.notifySubscribers();
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }

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
