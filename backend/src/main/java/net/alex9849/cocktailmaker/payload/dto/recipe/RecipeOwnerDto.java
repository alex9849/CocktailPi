package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.user.User;

public class RecipeOwnerDto {
    private long id;
    private String name;

    public RecipeOwnerDto() {}

    public RecipeOwnerDto(User user) {
        this.id = user.getId();
        this.name = user.getUsername();
    }

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
}
