package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class RecipeIngredientId implements Serializable {

    @Column(name = "recipe_id")
    private Long RecipeId;

    @Column(name = "ingredient_id")
    private Long IngredientId;

    @Column(name = "index")
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Long getRecipeId() {
        return RecipeId;
    }

    public void setRecipeId(Long recipeId) {
        RecipeId = recipeId;
    }

    public Long getIngredientId() {
        return IngredientId;
    }

    public void setIngredientId(Long ingredientId) {
        IngredientId = ingredientId;
    }
}
