package net.alex9849.cocktailmaker.config;

import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.repository.UserRepository;
import net.alex9849.cocktailmaker.service.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Initializer {

    @Bean
    public ApplicationRunner userInitializer(UserService userService, UserRepository userRepository) {
        return args -> {
            if(userRepository.count() != 0) {
                return;
            }
            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@localhost.local");
            user.setFirstname("Admin");
            user.setLastname("Example");
            user.setAuthority(ERole.ROLE_ADMIN);
            user.setAccountNonLocked(true);
            user.setPassword("123456");
            userService.createUser(user);

        };
    }

}


