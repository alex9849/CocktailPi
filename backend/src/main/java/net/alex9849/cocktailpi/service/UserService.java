package net.alex9849.cocktailpi.service;

import jakarta.annotation.PostConstruct;
import net.alex9849.cocktailpi.model.user.ERole;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.user.UserDto;
import net.alex9849.cocktailpi.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private User systemUser;

    @Value("${alex9849.app.demoMode}")
    private boolean isDemoMode;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IngredientService ingredientService;

    @Autowired
    PasswordEncoder encoder;

    @PostConstruct
    public void postConstruct() {
        User system = new User();
        system.setId(-1);
        system.setUsername("System");
        system.setAccountNonLocked(true);
        system.setAuthority(ERole.ROLE_ADMIN);
        this.systemUser = system;
    }

    /**
     *
     * @param user A user with a unencoded password
     * @return the created user
     */
    public User createUser(User user) {
        if(userRepository.findByUsernameIgnoringCase(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken!");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.create(user);
    }

    /**
     *
     * @param user A user object
     * @param encodePassword If true the password in the user-object will be encoded
     * @return the updated user-object
     */
    public User updateUser(User user, boolean encodePassword) {
        Optional<User> userWithId = userRepository.findById(user.getId());
        if(!userWithId.isPresent()) {
            throw new IllegalArgumentException("User doesn't exist!");
        }
        if(isDemoMode && user.getId() == 1) {
            throw new IllegalArgumentException("The admin-user can't be edited in demomode!");
        }
        Optional<User> userWithUsername = userRepository.findByUsernameIgnoringCase(user.getUsername());
        if(userWithUsername.isPresent() && !Objects.equals(userWithUsername.get().getId(), user.getId())) {
            throw new IllegalArgumentException("Username already taken!");
        }
        if(encodePassword) {
            user.setPassword(encoder.encode(user.getPassword()));
        }
        userRepository.update(user);
        return user;
    }

    public void deleteUser(long id) {
        if(isDemoMode && id == 1) {
            throw new IllegalArgumentException("The admin-user can't be deleted in demomode!");
        }
        userRepository.delete(id);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User getSystemUser() {
        return systemUser;
    }

    public ERole toRole(int level) {
        for(ERole role : ERole.values()) {
            if(role.getLevel() == level) {
                return role;
            }
        }
        return ERole.ROLE_USER;
    }

    public User fromDto(UserDto.Request.Create userDto) {
        if(userDto == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        user.setAuthority(toRole(userDto.getAdminLevel()));
        return user;
    }
}
