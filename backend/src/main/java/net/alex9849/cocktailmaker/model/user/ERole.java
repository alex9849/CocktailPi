package net.alex9849.cocktailmaker.model.user;

import org.springframework.security.core.GrantedAuthority;

public enum ERole implements GrantedAuthority {
    ROLE_USER("USER"), ROLE_ADMIN("ADMIN");

    private String roleName;

    ERole(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String getAuthority() {
        return roleName;
    }
}
