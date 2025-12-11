package net.alex9849.cocktailpi.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailpi.config.JwtUtils;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.user.UserDto;
import net.alex9849.cocktailpi.payload.request.LoginRequest;
import net.alex9849.cocktailpi.payload.response.JwtResponse;
import net.alex9849.cocktailpi.service.AuthService;
import net.alex9849.cocktailpi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.authUser(loginRequest.getUsername(), loginRequest.getPassword(),
                loginRequest.isRemember());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new JwtResponse(token,
                jwtUtils.getExpirationDateFromJwtToken(token), new UserDto.Response.Detailed(user)));
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
}
