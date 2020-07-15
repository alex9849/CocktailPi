package net.alex9849.cocktailmaker.model;

public enum ERole {
    ROLE_USER("user"), ROLE_ADMIN("admin");

    private String roleName;
    ERole(String roleName) {
        this.roleName = roleName;
    }

    public String roleName() {
        return this.roleName;
    }
}
