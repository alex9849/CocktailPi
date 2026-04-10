package net.alex9849.cocktailpi.model.cocktail;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.model.user.User;

import java.util.List;

@Getter @Setter
public class CocktailProgress {
    private Recipe recipe;
    private int progress;
    private User user;
    private State previousState;
    private State state;
    private List<ProductionStepIngredient> currentIngredientsToAddManually;
    private String writtenInstruction;
    private long loadCellValue;
    private boolean isShowLoadCellValue;


    public void setCurrentWrittenInstruction(String message) {
        this.writtenInstruction = message;
    }


    public enum State {
        RUNNING, MANUAL_INGREDIENT_ADD, MANUAL_ACTION_REQUIRED, CANCELLED, FINISHED, ERROR, READY_TO_START
    }
}
