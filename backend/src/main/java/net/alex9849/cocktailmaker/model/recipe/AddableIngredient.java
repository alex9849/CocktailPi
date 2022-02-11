package net.alex9849.cocktailmaker.model.recipe;

public abstract class AddableIngredient extends Ingredient {
    private int alcoholContent;
    private boolean inBar;

    public AddableIngredient(long id, Long parentGroupId) {
        super(id, parentGroupId);
    }

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
