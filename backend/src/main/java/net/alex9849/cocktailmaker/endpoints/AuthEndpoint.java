package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.config.JwtUtils;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.user.UserDto;
import net.alex9849.cocktailmaker.payload.request.LoginRequest;
import net.alex9849.cocktailmaker.payload.response.JwtResponse;
import net.alex9849.cocktailmaker.service.AuthService;
import net.alex9849.cocktailmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthEndpoint {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthService authService;

    @Autowired
    JwtUtils jwtUtils;


    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.authUser(loginRequest.getUsername(), loginRequest.getPassword());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new JwtResponse(token,
                jwtUtils.getExpirationDateFromJwtToken(token), new UserDto(user)));
    }

    @RequestMapping(value = "refreshToken", method = RequestMethod.GET)
    public ResponseEntity<?> authenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponse(jwt,
                jwtUtils.getExpirationDateFromJwtToken(jwt), new UserDto(user)));
    }
}
