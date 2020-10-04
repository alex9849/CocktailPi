package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.user.UserDto;
import net.alex9849.cocktailmaker.payload.request.UpdateUserRequest;
import net.alex9849.cocktailmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserEndpoint {

    @Autowired
    UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto createUser, UriComponentsBuilder uriBuilder) {
        User user = userService.fromDto(createUser);
        user.setAuthority(userService.toRole(createUser.getAdminLevel()));
        user = userService.createUser(user);
        UriComponents uriComponents = uriBuilder.path("/api/user/{id}").buildAndExpand(user.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @RequestMapping(value = {"{id}", "current"}, method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable(value = "id", required = false) Long userId, @Valid @RequestBody UpdateUserRequest updateUserRequest, HttpServletRequest request) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User updateUser = userService.fromDto(updateUserRequest.getUserDto());
        if(userId == null) {
            userId = principal.getId();
        } else {
            if(!principal.getAuthorities().contains(ERole.ROLE_ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        User beforeUpdate = userService.getUser(userId);
        if(beforeUpdate == null) {
            return ResponseEntity.notFound().build();
        }
        if(principal.getAuthorities().contains(ERole.ROLE_ADMIN)) {
            updateUser.setAuthority(userService.toRole(updateUserRequest.getUserDto().getAdminLevel()));
        } else {
            updateUser.setAuthority(beforeUpdate.getAuthority());
            updateUser.setAccountNonLocked(beforeUpdate.isAccountNonLocked());
        }
        //If user wants to update his password update it. Otherwise fill in the old encrypted password
        if(!updateUserRequest.isUpdatePassword()) {
            updateUser.setPassword(beforeUpdate.getPassword());
        }
        updateUser.setId(userId);
        return ResponseEntity.ok(new UserDto(userService.updateUser(updateUser, updateUserRequest.isUpdatePassword())));
    }



    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUser(@PathVariable("id") long userId) {
        if(userService.getUser(userId) == null) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = {"{id}", "current"}, method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable(value = "id", required = false) Long userId, HttpServletRequest request) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userId == null) {
            userId = principal.getId();
        } else {
            if(!principal.getAuthorities().contains(ERole.ROLE_ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
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
