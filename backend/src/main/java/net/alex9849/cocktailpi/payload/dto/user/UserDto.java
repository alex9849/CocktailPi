package net.alex9849.cocktailpi.payload.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import net.alex9849.cocktailpi.model.user.User;
import org.springframework.beans.BeanUtils;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDto {
    public interface Id { long getId(); }
    public interface Username { @NotNull @Size(min = 3, max = 20) String getUsername(); }
    public interface IsAccountNonLocked { @NotNull boolean isAccountNonLocked(); }
    public interface Password { @NotBlank @Size(min = 6, max = 40) String getPassword(); }
    public interface AdminLevel { int getAdminLevel(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        public static class Create implements Username, IsAccountNonLocked, Password, AdminLevel {
            String username;
            boolean isAccountNonLocked;
            String password;
            int adminLevel;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements Id, Username, IsAccountNonLocked, AdminLevel {
            long id;
            String username;
            boolean isAccountNonLocked;
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
