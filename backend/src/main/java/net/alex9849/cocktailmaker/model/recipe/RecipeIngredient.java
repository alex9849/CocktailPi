package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {

    @EmbeddedId
    private RecipeIngredientId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("RecipeId")
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne()
    @MapsId("IngredientId")
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Min(1)
    private int amount;

    public RecipeIngredientId getId() {
        return id;
    }

    public void setId(RecipeIngredientId id) {
        this.id = id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    /**
     *
     * @return amount in ml
     */
    public int getAmount() {
        return amount;
    }

    /**
     *
     * @param amount in ml
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }
}
