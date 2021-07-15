package net.alex9849.cocktailmaker.model.recipe;

public class RecipeIngredient {

    private Long recipeId;
    private Long ingredientId;
    private Long productionStep;
    private Ingredient ingredient;
    private int amount;

    public Long getRecipeId() {
        return recipeId;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public Long getProductionStep() {
        return productionStep;
    }

    public void setProductionStep(Long productionStep) {
        this.productionStep = productionStep;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
