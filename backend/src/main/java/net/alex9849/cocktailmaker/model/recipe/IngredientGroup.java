package net.alex9849.cocktailmaker.model.recipe;

import net.alex9849.cocktailmaker.service.IngredientService;
import net.alex9849.cocktailmaker.utils.SpringUtility;

import javax.persistence.DiscriminatorValue;
import java.util.HashSet;
import java.util.Set;

@DiscriminatorValue("IngredientGroup")
public class IngredientGroup extends Ingredient {
    private Set<Ingredient> children;

    public Unit getUnit() {
        return Unit.MILLILITER;
    }

    public Set<Ingredient> getChildren() {
        if(children == null) {
            //Lazy load
            IngredientService iService = SpringUtility.getBean(IngredientService.class);
            this.children = iService.getGroupChildren(this.getId());
        }
        return children;
    }

    public int getMinAlcoholContent() {
        if(this.getChildren().isEmpty()) {
            return 0;
        }
        int alcoholContent = Integer.MAX_VALUE;
        for(Ingredient ingredient : this.getChildren()) {
            if(ingredient instanceof IngredientGroup) {
                alcoholContent = Math.min(alcoholContent, ((IngredientGroup) ingredient).getMinAlcoholContent());
            } else if (ingredient instanceof AddableIngredient) {
                alcoholContent = Math.min(alcoholContent, ((AddableIngredient) ingredient).getAlcoholContent());
            } else {
                throw new IllegalStateException("Unknown Ingredient type: " + ingredient.getClass().getName());
            }
        }
        return Math.max(0, alcoholContent);
    }

    public int getMaxAlcoholContent() {
        if(this.getChildren().isEmpty()) {
            return 0;
        }
        int alcoholContent = 0;
        for(Ingredient ingredient : this.getChildren()) {
            if(ingredient instanceof IngredientGroup) {
                alcoholContent = Math.max(alcoholContent, ((IngredientGroup) ingredient).getMinAlcoholContent());
            } else if (ingredient instanceof AddableIngredient) {
                alcoholContent = Math.max(alcoholContent, ((AddableIngredient) ingredient).getAlcoholContent());
            } else {
                throw new IllegalStateException("Unknown Ingredient type: " + ingredient.getClass().getName());
            }
        }
        return alcoholContent;
    }

    public Set<AddableIngredient> getMatchingAddableIngredients() {
        Set<AddableIngredient> leaves = new HashSet<>();
        for(Ingredient ingredient : this.getChildren()) {
            if(ingredient instanceof IngredientGroup) {
                leaves.addAll(((IngredientGroup) ingredient).getMatchingAddableIngredients());
            } else if(ingredient instanceof AddableIngredient) {
                leaves.add((AddableIngredient) ingredient);
            } else {
                throw new IllegalStateException("Unknown Ingredient type: " + ingredient.getClass().getName());
            }
        }
        return leaves;
    }

    @Override
    public boolean isInBar() {
        for(Ingredient ingredient : this.getChildren()) {
            if(ingredient.isInBar()) {
                return true;
            }
        }
        return false;
    }
}
