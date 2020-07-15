package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.ERole;
import net.alex9849.cocktailmaker.model.Role;
import net.alex9849.cocktailmaker.model.User;
import net.alex9849.cocktailmaker.repository.RoleRepository;
import net.alex9849.cocktailmaker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    public User createUser(User user) {
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken!");
        }
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("A user with that Email already exists!");
        }
        return userRepository.save(user);
    }

    public Set<Role> toRoles(Set<String> stringRoles) {
        Set<Role> roles = new HashSet<>();
        if (stringRoles == null || stringRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            stringRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        return roles;
    }
}
