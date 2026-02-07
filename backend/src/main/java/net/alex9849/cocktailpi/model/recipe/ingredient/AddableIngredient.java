package net.alex9849.cocktailpi.model.recipe.ingredient;

public abstract class AddableIngredient extends Ingredient {
    private int alcoholContent;
    private boolean inBar;

    private boolean hasImage;
    private Double bottlePrice;

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

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Double getBottlePrice() {
        return bottlePrice;
    }

    public void setBottlePrice(Double bottlePrice) {
        this.bottlePrice = bottlePrice;
    }
}
