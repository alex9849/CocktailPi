package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.config.JwtUtils;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    private boolean onlyPasswordAuth = false;

    /**
     * Genereates a JSON-Web-Token if the username and the password are corrent.
     * Otherwise an Exception will be thrown
     * @param username The username of the user
     * @param password The password of the user
     * @return A JSON-Web-Token
     */
    public String authUser(String username, String password, boolean remember) {
        String authUsername = username;
        if (onlyPasswordAuth) {
            authUsername = userRepository.findAll().stream()
                    .filter(u -> encoder.matches(password, u.getPassword()))
                    .max(Comparator.comparingInt(u -> u.getAuthority().getLevel()))
                    .map(User::getUsername)
                    .orElseThrow(() -> new BadCredentialsException("Invalid password"));
        } else if (username == null) {
            throw new BadCredentialsException("Invalid username");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authUsername, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication, remember);
    }

}
