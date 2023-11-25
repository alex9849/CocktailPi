package net.alex9849.cocktailpi.model.recipe.ingredient;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailpi.service.IngredientService;
import net.alex9849.cocktailpi.utils.SpringUtility;

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
            this.children = new HashSet<>(iService.getGroupChildren(this.getId()));
        }
        return children;
    }

    public Integer getMinAlcoholContent() {
        int alcoholContent = Integer.MAX_VALUE;
        boolean resultValid = false;
        for(Ingredient ingredient : this.getChildren()) {
            if(ingredient instanceof IngredientGroup) {
                IngredientGroup childGroup = (IngredientGroup) ingredient;
                Integer childAlcoholContent = childGroup.getMinAlcoholContent();
                if(childAlcoholContent == null) {
                    continue;
                }
                alcoholContent = Math.min(alcoholContent, childAlcoholContent);
                resultValid = true;
            } else if (ingredient instanceof AddableIngredient) {
                alcoholContent = Math.min(alcoholContent, ((AddableIngredient) ingredient).getAlcoholContent());
                resultValid = true;
            } else {
                throw new IllegalStateException("Unknown Ingredient type: " + ingredient.getClass().getName());
            }
        }
        if(!resultValid) {
            return null;
        }
        return Math.max(0, alcoholContent);
    }

    public Integer getMaxAlcoholContent() {
        int alcoholContent = 0;
        boolean resultValid = false;
        for(Ingredient ingredient : this.getChildren()) {
            if(ingredient instanceof IngredientGroup) {
                IngredientGroup childGroup = (IngredientGroup) ingredient;
                Integer childAlcoholContent = childGroup.getMaxAlcoholContent();
                if(childAlcoholContent == null) {
                    continue;
                }
                alcoholContent = Math.max(alcoholContent, childAlcoholContent);
                resultValid = true;
            } else if (ingredient instanceof AddableIngredient) {
                alcoholContent = Math.max(alcoholContent, ((AddableIngredient) ingredient).getAlcoholContent());
                resultValid = true;
            } else {
                throw new IllegalStateException("Unknown Ingredient type: " + ingredient.getClass().getName());
            }
        }
        if(!resultValid) {
            return null;
        }
        return alcoholContent;
    }

    public Set<AddableIngredient> getAddableIngredientChildren() {
        Set<AddableIngredient> leaves = new HashSet<>();
        for(Ingredient ingredient : this.getChildren()) {
            if(ingredient instanceof IngredientGroup) {
                leaves.addAll(((IngredientGroup) ingredient).getAddableIngredientChildren());
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

    @Override
    public boolean isOnPump() {
        for(Ingredient leaf : this.getChildren()) {
            if(leaf.isOnPump()) {
                return true;
            }
        }
        return false;
    }
}
