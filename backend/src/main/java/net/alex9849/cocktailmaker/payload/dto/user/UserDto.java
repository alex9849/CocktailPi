package net.alex9849.cocktailmaker.payload.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.alex9849.cocktailmaker.model.user.User;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserDto {
    private Long id;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotNull
    @Size(max = 20)
    private String firstname;

    @NotNull
    @Size(max = 20)
    private String lastname;

    @NotNull
    private boolean isAccountNonLocked;

    @Size(max = 50)
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private int adminLevel;

    public UserDto() {}

    public UserDto(User user) {
        BeanUtils.copyProperties(user, this);
        if(user.getAuthority() != null) {
            this.adminLevel = user.getAuthority().getLevel();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }
}
