package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.user.UserDto;
import net.alex9849.cocktailmaker.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Value("${alex9849.app.demoMode}")
    private boolean isDemoMode;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IngredientService ingredientService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    EntityManager entityManager;

    /**
     *
     * @param user A user with a unencoded password
     * @return the created user
     */
    public User createUser(User user) {
        if(userRepository.findByUsernameIgnoringCase(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken!");
        }
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("A user with that Email already exists!");
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
        Optional<User> userWithEMail = userRepository.findByEmail(user.getEmail());
        if(userWithEMail.isPresent() && !Objects.equals(userWithEMail.get().getId(), user.getId())) {
            throw new IllegalArgumentException("A user with that Email already exists!");
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

    public static ERole toRole(int level) {
        for(ERole role : ERole.values()) {
            if(role.getLevel() == level) {
                return role;
            }
        }
        return ERole.ROLE_USER;
    }

    public static User fromDto(UserDto userDto) {
        if(userDto == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        user.setAuthority(toRole(userDto.getAdminLevel()));
        return user;
    }
}
