package net.alex9849.cocktailmaker.model.recipe;

import net.alex9849.cocktailmaker.model.Category;
import net.alex9849.cocktailmaker.model.user.User;

import java.util.Date;
import java.util.List;

public class Recipe {
    private long id;
    private String name;
    private boolean hasImage;
    private User owner;
    private Long ownerId;
    private String description;
    private Date lastUpdate;
    private long defaultAmountToFill;
    private List<ProductionStep> productionSteps;
    private List<Category> categories;

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

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<ProductionStep> getProductionSteps() {
        return productionSteps;
    }

    public void setProductionSteps(List<ProductionStep> productionSteps) {
        this.productionSteps = productionSteps;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public long getDefaultAmountToFill() {
        return defaultAmountToFill;
    }

    public void setDefaultAmountToFill(long defaultAmountToFill) {
        this.defaultAmountToFill = defaultAmountToFill;
    }
}
