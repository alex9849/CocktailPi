package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("IngredientGroup")
public class IngredientGroup {
    private long id;
    private String name;
    private Long parentGroupId;

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

    public Long getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(Long parentGroupId) {
        this.parentGroupId = parentGroupId;
    }
}
