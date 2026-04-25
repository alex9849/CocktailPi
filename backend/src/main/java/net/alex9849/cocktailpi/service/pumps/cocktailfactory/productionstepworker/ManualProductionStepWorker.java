package net.alex9849.cocktailpi.service.pumps.cocktailfactory.productionstepworker;

import net.alex9849.cocktailpi.model.pump.RelativeLoadCellReader;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.service.pumps.cocktailfactory.CocktailFactory;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

public class ManualProductionStepWorker extends AbstractProductionStepWorker
        implements ManualFinishable {
    private final List<ProductionStepIngredient> productionStepInstructions;
    private final RelativeLoadCellReader relativeLoadCellReader;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> notifierTask;
    private boolean showLoadCellValue;

    public ManualProductionStepWorker(CocktailFactory cocktailFactory, RelativeLoadCellReader relativeLoadCellReader, List<ProductionStepIngredient> productionStepInstructions) {
        super(cocktailFactory);
        if(productionStepInstructions.isEmpty()) {
            throw new IllegalArgumentException("ProductionStepInstructions must be non-empty!");
        }
        this.productionStepInstructions = productionStepInstructions;
        this.relativeLoadCellReader = relativeLoadCellReader;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        this.showLoadCellValue = false;
        if (this.relativeLoadCellReader != null) {
            for (ProductionStepIngredient ps : this.productionStepInstructions) {
                Ingredient.Unit unit = ps.getIngredient().getUnit();
                if (unit == Ingredient.Unit.GRAM || unit == Ingredient.Unit.MILLILITER) {
                    this.showLoadCellValue = true;
                    break;
                }
            }
        }

    }

    @Override
    public void start() {
        super.start();
        if (this.relativeLoadCellReader != null) {
            try {
                this.relativeLoadCellReader.tare(true);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                this.getCocktailFactory().cancelCocktail(true);
            }
        }
        this.notifySubscribers();
        if(this.showLoadCellValue) {
            this.notifierTask = this.scheduler.scheduleAtFixedRate(this::notifySubscribers, 100, 200, TimeUnit.MILLISECONDS);
        }
    }

    public int getSize() {
        return this.productionStepInstructions.size();
    }

    public void continueProduction() {
        if(!this.isStarted()) {
            return;
        }
        this.shutdown();
        this.setFinished();
    }

    protected void shutdown() {
        this.scheduler.shutdownNow();
        try {
            if(!this.scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                LoggerFactory.getLogger(this.getClass()).error("ManualProductionStepWorker scheduler didn't terminate in 5 seconds!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean cancel() {
        if(!super.cancel()) {
            return false;
        }
        this.shutdown();
        return true;
    }

    @Override
    public ManualStepProgress getProgress() {
        ManualStepProgress manualStepProgress = new ManualStepProgress();
        manualStepProgress.setPercentCompleted(0);
        manualStepProgress.setIngredientsToBeAdded(this.productionStepInstructions);
        manualStepProgress.setFinished(this.isFinished());
        manualStepProgress.setShowLoadCellValue(false);
        if (this.relativeLoadCellReader != null) {
            manualStepProgress.setShowLoadCellValue(showLoadCellValue);
            if (showLoadCellValue) {
                try {
                    manualStepProgress.setLoadCellValue(this.relativeLoadCellReader.readCurrent(5).get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    this.getCocktailFactory().cancelCocktail(true);
                }
            } else {
                manualStepProgress.setLoadCellValue(0);
            }
        }
        return manualStepProgress;
    }
}
