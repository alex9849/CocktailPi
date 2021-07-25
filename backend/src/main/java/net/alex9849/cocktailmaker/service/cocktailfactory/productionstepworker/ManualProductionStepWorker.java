package net.alex9849.cocktailmaker.service.cocktailfactory.productionstepworker;

import net.alex9849.cocktailmaker.model.recipe.ManualIngredient;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;

import java.util.List;

public class ManualProductionStepWorker extends AbstractProductionStepWorker {
    private final List<RecipeIngredient> productionStepInstructions;
    private int currentIndex = 0;
    private boolean finished = false;

    public ManualProductionStepWorker(List<RecipeIngredient> productionStepInstructions) {
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
            this.finished = true;
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
        this.finished = true;
        this.notifySubscribers();
    }

    @Override
    public Mode getMode() {
        return Mode.MANUAL;
    }

    public int getAmountFilled() {
        int amountFilled = 0;
        for(int i = 0; i < this.currentIndex; i++) {
            RecipeIngredient recipeIngredient = this.productionStepInstructions.get(this.currentIndex);
            if(recipeIngredient.getIngredient() instanceof ManualIngredient
                    && !recipeIngredient.getIngredient().isAddToVolume()) {
                continue;
            }
            amountFilled += this.productionStepInstructions.get(this.currentIndex).getAmount();
        }
        return amountFilled;
    }

    public int getAmountToFill() {
        return this.productionStepInstructions.stream()
                .mapToInt(RecipeIngredient::getAmount).sum();
    }

    @Override
    public ManualStepProgress getProgress() {
        ManualStepProgress manualStepProgress = new ManualStepProgress();
        manualStepProgress.setPercentCompleted((int) (this.getAmountFilled() * 100d )/ getAmountToFill());
        manualStepProgress.setIngredientToBeAdded(this.productionStepInstructions.get(this.currentIndex));
        manualStepProgress.setFinished(this.finished);
        return manualStepProgress;
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }
}
