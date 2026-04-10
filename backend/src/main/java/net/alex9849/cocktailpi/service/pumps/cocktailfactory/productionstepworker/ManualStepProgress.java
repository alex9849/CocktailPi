package net.alex9849.cocktailpi.service.pumps.cocktailfactory.productionstepworker;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;

import java.util.List;

@Getter @Setter
public class ManualStepProgress extends StepProgress {
    private List<ProductionStepIngredient> ingredientsToBeAdded;
    private long loadCellValue;
    private boolean showLoadCellValue;
}
