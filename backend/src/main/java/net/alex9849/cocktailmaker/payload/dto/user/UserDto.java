package net.alex9849.cocktailmaker.payload.dto.user;

import lombok.*;
import net.alex9849.cocktailmaker.model.user.User;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDto {
    public interface Id { long getId(); }
    public interface Username { @NotNull @Size(min = 3, max = 20) String getUsername(); }
    public interface Firstname { @NotNull @Size(max = 20)String getFirstname(); }
    public interface Lastname { @NotNull @Size(max = 20) String getLastname(); }
    public interface IsAccountNonLocked { @NotNull boolean isAccountNonLocked(); }
    public interface Email { @Size(max = 50) @NotBlank @javax.validation.constraints.Email String getEmail(); }
    public interface Password { @NotBlank @Size(min = 6, max = 40) String getPassword(); }
    public interface AdminLevel { int getAdminLevel(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        public static class Create implements Username, Firstname, Lastname, IsAccountNonLocked, Email, Password, AdminLevel {
            String username;
            String firstname;
            String lastname;
            boolean isAccountNonLocked;
            String email;
            String password;
            int adminLevel;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements Id, Username, Firstname, Lastname, IsAccountNonLocked, Email, AdminLevel {
            long id;
            String username;
            String firstname;
            String lastname;
            boolean isAccountNonLocked;
            String email;
            int adminLevel;

            public Detailed(User user) {
                BeanUtils.copyProperties(user, this);
                if(user.getAuthority() != null) {
                    this.adminLevel = user.getAuthority().getLevel();
                }
            }
        }
    }
}
