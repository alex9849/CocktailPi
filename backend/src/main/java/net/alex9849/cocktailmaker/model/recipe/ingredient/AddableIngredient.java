package net.alex9849.cocktailmaker.model.recipe.ingredient;

public abstract class AddableIngredient extends Ingredient {
    private int alcoholContent;
    private boolean inBar;

    public int getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(int alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    @Override
    public boolean isInBar() {
        return this.inBar;
    }

    public void setInBar(boolean inBar) {
        this.inBar = inBar;
    }
}
