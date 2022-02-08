package net.alex9849.cocktailmaker.model.recipe;

public abstract class Ingredient extends IngredientGroup {
    private int alcoholContent;
    private boolean inBar;

    public int getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(int alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    public boolean isInBar() {
        return inBar;
    }

    public void setInBar(boolean inBar) {
        this.inBar = inBar;
    }
}
