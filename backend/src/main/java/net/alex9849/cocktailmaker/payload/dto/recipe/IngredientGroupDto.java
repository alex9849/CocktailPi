package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.IngredientGroup;

import java.util.Set;
import java.util.stream.Collectors;

public class IngredientGroupDto extends IngredientDto {
    private Set<Long> leafIds;
    private boolean inBar;
    private int minAlcoholContent;
    private int maxAlcoholContent;

    public IngredientGroupDto() {}

    public IngredientGroupDto(IngredientGroup ingredient) {
        super(ingredient);
        leafIds = ingredient.getAddableIngredientChildren().stream()
                .map(Ingredient::getId).collect(Collectors.toSet());
        inBar = ingredient.isInBar();
        minAlcoholContent = ingredient.getMinAlcoholContent();
        maxAlcoholContent = ingredient.getMaxAlcoholContent();
    }

    public Set<Long> getLeafIds() {
        return leafIds;
    }

    public int getMinAlcoholContent() {
        return minAlcoholContent;
    }

    public int getMaxAlcoholContent() {
        return maxAlcoholContent;
    }

    @Override
    public String getType() {
        return "group";
    }

    @Override
    public boolean isInBar() {
        return inBar;
    }

    @Override
    public Ingredient.Unit getUnit() {
        return Ingredient.Unit.MILLILITER;
    }
}
