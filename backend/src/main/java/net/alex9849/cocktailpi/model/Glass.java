package net.alex9849.cocktailpi.model;

public class Glass {
    private long id;
    private String name;
    private long size;
    private boolean isDefault;
    private boolean isUseForSingleIngredients;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isUseForSingleIngredients() {
        return isUseForSingleIngredients;
    }

    public void setUseForSingleIngredients(boolean useForSingleIngredients) {
        isUseForSingleIngredients = useForSingleIngredients;
    }
}
