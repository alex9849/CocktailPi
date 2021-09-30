package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;

import java.util.List;

public class ManualProductionStepWorker extends AbstractProductionStepWorker {
    protected boolean started = false;
    private final List<RecipeIngredient> productionStepInstructions;

    public ManualProductionStepWorker(List<RecipeIngredient> productionStepInstructions) {
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
    public Mode getMode() {
        return Mode.MANUAL;
    }

    public int getAmountToFill() {
        return this.productionStepInstructions.stream()
                .mapToInt(RecipeIngredient::getAmount).sum();
    }

    @Override
    public ManualStepProgress getProgress() {
        ManualStepProgress manualStepProgress = new ManualStepProgress();
        manualStepProgress.setPercentCompleted(0);
        manualStepProgress.setIngredientsToBeAdded(this.productionStepInstructions);
        manualStepProgress.setFinished(this.isFinished());
        return manualStepProgress;
    }
}
