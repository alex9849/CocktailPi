package net.alex9849.cocktailpi.model.user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

public enum ERole implements GrantedAuthority {
    ROLE_USER("USER", 0),
    ROLE_RECIPE_CREATOR("RECIPE_CREATOR", 1),
    ROLE_PUMP_INGREDIENT_EDITOR("PUMP_INGREDIENT_EDITOR", 2),
    ROLE_ADMIN("ADMIN", 3),
    ROLE_SUPER_ADMIN("SUPER_ADMIN", 4);

    private final String roleName;

    @Getter
    private final int level;

    ERole(String roleName, int level) {
        this.roleName = roleName;
        this.level = level;
    }

    @Override
    public String getAuthority() {
        return roleName;
    }

}
