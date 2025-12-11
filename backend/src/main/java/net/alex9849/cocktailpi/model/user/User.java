package net.alex9849.cocktailpi.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class User implements UserDetails {
    private long id;
    private String username;
    private boolean isAccountNonLocked;
    private ERole role;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Set<ERole> getAuthorities() {
        Set<ERole> roles = new HashSet<>();
        for (ERole role : ERole.values()) {
            if (this.role.getLevel() >= role.getLevel()) {
                roles.add(role);
            }
        }
        return roles;
    }

    public ERole getAuthority() {
        return this.role;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthority(ERole role) {
        this.role = role;
    }

    public void setAccountNonLocked(boolean isAccountNonLocked) {
        this.isAccountNonLocked = isAccountNonLocked;
    }
}
