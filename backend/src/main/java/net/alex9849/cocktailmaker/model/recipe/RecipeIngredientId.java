package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RecipeIngredientId implements Serializable {

    @Column(name = "recipe_id")
    private Long RecipeId;

    @Column(name = "ingredient_id")
    private Long IngredientId;

    @Column(name = "production_step")
    private int productionStep;

    public int getProductionStep() {
        return productionStep;
    }

    public void setProductionStep(int index) {
        this.productionStep = index;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeIngredientId that = (RecipeIngredientId) o;
        return productionStep == that.productionStep &&
                Objects.equals(RecipeId, that.RecipeId) &&
                Objects.equals(IngredientId, that.IngredientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(RecipeId, IngredientId, productionStep);
    }
}
