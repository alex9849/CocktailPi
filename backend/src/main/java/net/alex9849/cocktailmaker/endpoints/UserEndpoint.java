package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.User;
import net.alex9849.cocktailmaker.payload.dto.UserDto;
import net.alex9849.cocktailmaker.security.services.UserDetailsImpl;
import net.alex9849.cocktailmaker.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserEndpoint {

    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder encoder;

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto createUser, UriComponentsBuilder uriBuilder) {
        User user = new User();
        BeanUtils.copyProperties(createUser, user);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRoles(userService.toRoles(createUser.getRole()));
        user = userService.createUser(user);
        UriComponents uriComponents = uriBuilder.path("/api/user/{id}").buildAndExpand(user.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public ResponseEntity<?> updateUser(@PathVariable("id") long userId, @RequestBody User user) {
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUser(@PathVariable("id") long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "current", method = RequestMethod.GET)
    public ResponseEntity<?> getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUser(userDetails.getId());
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserDto(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable("id") long userId) {
        User user = userService.getUser(userId);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserDto(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getUsers() {
        List<UserDto> userDtoList = userService.getUsers()
                .stream().map(UserDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtoList);
    }
}
