package net.alex9849.cocktailpi.repository;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.user.ERole;
import net.alex9849.cocktailpi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryTest extends BackendIntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUpdateFindAndDeleteUser() {
        User user = new User();
        user.setUsername("RepoUser");
        user.setPassword("secret");
        user.setAccountNonLocked(true);
        user.setAuthority(ERole.ROLE_USER);

        User created = userRepository.create(user);
        Optional<User> found = userRepository.findById(created.getId());
        assertTrue(found.isPresent());

        created.setUsername("RepoUserUpdated");
        assertTrue(userRepository.update(created));

        Optional<User> byName = userRepository.findByUsernameIgnoringCase("repouserupdated");
        assertTrue(byName.isPresent());
        assertEquals(created.getId(), byName.get().getId());

        assertTrue(userRepository.delete(created.getId()));
    }
}
