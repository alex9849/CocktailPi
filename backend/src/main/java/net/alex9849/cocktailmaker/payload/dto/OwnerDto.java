package net.alex9849.cocktailmaker.payload.dto;

import net.alex9849.cocktailmaker.model.user.User;

public class OwnerDto {
    private long id;
    private String username;

    public OwnerDto() {}

    public OwnerDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
