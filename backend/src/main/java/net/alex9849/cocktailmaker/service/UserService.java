package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.user.UserDto;
import net.alex9849.cocktailmaker.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User createUser(User user) {
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken!");
        }
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("A user with that Email already exists!");
        }
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        if(!userRepository.findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User doesn't exist!");
        }
        Optional<User> userWithUsername = userRepository.findByUsername(user.getUsername());
        if(userWithUsername.isPresent() && !Objects.equals(userWithUsername.get().getId(), user.getId())) {
            throw new IllegalArgumentException("Username already taken!");
        }
        Optional<User> userWithEMail = userRepository.findByEmail(user.getEmail());
        if(userWithEMail.isPresent() && !Objects.equals(userWithEMail.get().getId(), user.getId())) {
            throw new IllegalArgumentException("A user with that Email already exists!");
        }
        return userRepository.save(user);
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public Set<ERole> toRoles(Set<String> stringRoles) {
        Set<ERole> roles = new HashSet<>();
        if (stringRoles == null || stringRoles.isEmpty()) {
            roles.add(ERole.ROLE_USER);
        } else {
            stringRoles.forEach(role -> {
                switch (role) {
                    case "ADMIN":
                        roles.add(ERole.ROLE_ADMIN);
                        break;
                    default:
                        roles.add(ERole.ROLE_USER);
                }
            });
        }
        return roles;
    }

    public User fromDto(UserDto userDto) {
        if(userDto == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        user.setAuthorities(toRoles(userDto.getRoles()));
        return user;
    }
}
