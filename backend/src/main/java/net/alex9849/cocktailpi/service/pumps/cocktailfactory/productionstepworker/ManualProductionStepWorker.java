package net.alex9849.cocktailpi.service.pumps.cocktailfactory.productionstepworker;

import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.CocktailFactory;

import java.util.List;

public class ManualProductionStepWorker extends AbstractProductionStepWorker
        implements ManualFinishable {
    private final List<ProductionStepIngredient> productionStepInstructions;

    public ManualProductionStepWorker(CocktailFactory cocktailFactory, List<ProductionStepIngredient> productionStepInstructions) {
        super(cocktailFactory);
        if(productionStepInstructions.size() == 0) {
            throw new IllegalArgumentException("ProductionStepInstructions must be non-empty!");
        }
        this.productionStepInstructions = productionStepInstructions;
    }

    @Override
    public void start() {
        super.start();
        this.notifySubscribers();
    }

    public int getSize() {
        return this.productionStepInstructions.size();
    }

    public void continueProduction() {
        if(!this.isStarted()) {
            return;
        }
        this.setFinished();
    }

    @Override
    public boolean cancel() {
        return super.cancel();
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
