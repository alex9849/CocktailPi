package net.alex9849.cocktailmaker.model.user;

import org.springframework.security.core.GrantedAuthority;

public enum ERole implements GrantedAuthority {
    ROLE_USER("USER", 0), ROLE_PUMP_INGREDIENT_EDITOR("PUMP_INGREDIENT_EDITOR", 1), ROLE_ADMIN("ADMIN", 2);

    private String roleName;

    private int level;

    ERole(String roleName, int level) {
        this.roleName = roleName;
        this.level = level;
    }

    @Override
    public String getAuthority() {
        return roleName;
    }

    public int getLevel() {
        return level;
    }
}
