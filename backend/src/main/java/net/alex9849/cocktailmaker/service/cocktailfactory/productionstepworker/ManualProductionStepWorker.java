package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.recipe.ProductionStepIngredient;

import java.util.List;

public class ManualProductionStepWorker extends AbstractProductionStepWorker
        implements ManualFinishable {
    protected boolean started = false;
    private final List<ProductionStepIngredient> productionStepInstructions;

    public ManualProductionStepWorker(List<ProductionStepIngredient> productionStepInstructions) {
        if(productionStepInstructions.size() == 0) {
            throw new IllegalArgumentException("ProductionStepInstructions must be non-empty!");
        }
        this.productionStepInstructions = productionStepInstructions;
    }

    @Override
    public void start() {
        this.started = true;
        this.notifySubscribers();
    }

    public int getSize() {
        return this.productionStepInstructions.size();
    }

    public void continueProduction() {
        if(!this.started) {
            return;
        }
        this.setFinished();
    }

    @Override
    public void cancel() {}

    @Override
    public ManualStepProgress getProgress() {
        ManualStepProgress manualStepProgress = new ManualStepProgress();
        manualStepProgress.setPercentCompleted(0);
        manualStepProgress.setIngredientsToBeAdded(this.productionStepInstructions);
        manualStepProgress.setFinished(this.isFinished());
        return manualStepProgress;
    }
}
