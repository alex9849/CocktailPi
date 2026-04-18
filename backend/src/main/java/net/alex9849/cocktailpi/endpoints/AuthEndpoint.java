package net.alex9849.cocktailpi.endpoints;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import net.alex9849.cocktailpi.config.JwtUtils;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.user.UserDto;
import net.alex9849.cocktailpi.payload.request.LoginRequest;
import net.alex9849.cocktailpi.payload.response.JwtResponse;
import net.alex9849.cocktailpi.service.AuthService;
import net.alex9849.cocktailpi.service.SystemService;
import net.alex9849.cocktailpi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/")
public class AuthEndpoint {

    @Autowired
    AuthService authService;

    @Autowired
    JwtUtils jwtUtils;

    @Value("${alex9849.app.demoMode}")
    private boolean isDemoMode;


    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.authUser(loginRequest.getUsername(), loginRequest.getPassword(),
                    loginRequest.isRemember());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new JwtResponse(token,
                jwtUtils.getExpirationDateFromJwtToken(token), new UserDto.Response.Detailed(user)));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login data");
        }
    }

    @RequestMapping(value = "refreshToken", method = RequestMethod.GET)
    public ResponseEntity<?> authenticateUser(@RequestHeader("Authorization") String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        token = jwtUtils.parseJwt(token);
        String jwt = jwtUtils.generateJwtToken(authentication, jwtUtils.isRemember(token));

        return ResponseEntity.ok(new JwtResponse(jwt,
                jwtUtils.getExpirationDateFromJwtToken(jwt), new UserDto.Response.Detailed(user)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "passwordOnly", method = RequestMethod.PUT)
    public ResponseEntity<?> setPasswordOnly(@RequestBody ObjectNode passwordOnlyNode) {
        if(isDemoMode) {
            throw new RuntimeException("Not allowed in demo-mode!");
        }
        authService.setPasswordOnly(passwordOnlyNode.get("passwordOnly").asBoolean());
        return ResponseEntity.ok(authService.isPasswordOnly());
    }
}
