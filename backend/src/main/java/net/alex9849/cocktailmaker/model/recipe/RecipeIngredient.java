package net.alex9849.cocktailmaker.model.recipe;

public class RecipeIngredient {

    private long recipeId;
    private long ingredientId;
    private long productionStep;
    private Ingredient ingredient;
    private int amount;
    private boolean scale;

    public long getRecipeId() {
        return recipeId;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public long getProductionStep() {
        return productionStep;
    }

    public void setProductionStep(long productionStep) {
        this.productionStep = productionStep;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isScale() {
        return scale;
    }

    public void setScale(boolean scale) {
        this.scale = scale;
    }
}
