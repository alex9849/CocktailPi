package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;

import java.util.List;

public class ManualProductionStepWorker extends AbstractProductionStepWorker {
    private final List<RecipeIngredient> productionStepInstructions;
    private int currentIndex = 0;
    private boolean isFinished;

    ManualProductionStepWorker(List<RecipeIngredient> productionStepInstructions) {
        if(productionStepInstructions.size() == 0) {
            throw new IllegalArgumentException("ProductionStepInstructions must be non-empty!");
        }
        this.productionStepInstructions = productionStepInstructions;
    }

    @Override
    public void start() {
        this.notifySubscribers();
    }

    public boolean next() {
        if(this.productionStepInstructions.size() <= this.currentIndex + 1) {
            this.isFinished = true;
            return false;
        }
        this.currentIndex++;
        return true;
    }

    public int getSize() {
        return this.productionStepInstructions.size();
    }

    @Override
    public void cancel() {
        this.isFinished = true;
        this.notifySubscribers();
    }

    @Override
    public Mode getMode() {
        return Mode.MANUAL;
    }

    @Override
    protected ManualStepProgress getProgress() {
        int amountFilled = 0;
        for(int i = 0; i < this.currentIndex; i++) {
            amountFilled += this.productionStepInstructions.get(this.currentIndex).getAmount();
        }
        int amountToFill = this.productionStepInstructions.stream().mapToInt(RecipeIngredient::getAmount).sum();

        ManualStepProgress manualStepProgress = new ManualStepProgress();
        manualStepProgress.setPercentCompleted((int) (amountFilled * 100d )/ amountToFill);
        manualStepProgress.setIngredientToBeAdded(this.productionStepInstructions.get(this.currentIndex));
        return manualStepProgress;
    }

    @Override
    public boolean isFinished() {
        return this.isFinished;
    }
}
