package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.User;
import net.alex9849.cocktailmaker.payload.dto.UserDto;
import net.alex9849.cocktailmaker.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.xml.ws.Response;

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
        UriComponents uriComponents = uriBuilder.path("/{id}").buildAndExpand(user.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public Response<?> updateUser(@PathVariable("id") int userId, @RequestBody User user) {
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Response<?> deleteUser() {
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Response<?> getUsers() {
        return null;
    }
}
