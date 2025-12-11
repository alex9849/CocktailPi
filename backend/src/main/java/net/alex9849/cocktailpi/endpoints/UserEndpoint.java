package net.alex9849.cocktailpi.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailpi.model.user.ERole;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.user.UserDto;
import net.alex9849.cocktailpi.payload.request.UpdateUserRequest;
import net.alex9849.cocktailpi.service.IngredientService;
import net.alex9849.cocktailpi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/")
public class UserEndpoint {

    @Autowired
    UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto.Request.Create createUser, UriComponentsBuilder uriBuilder) {
        User user = userService.fromDto(createUser);
        user.setAuthority(userService.toRole(createUser.getAdminLevel()));

        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ERole authority = principal.getAuthority();

        if (authority.getLevel() < user.getAuthority().getLevel()) {
            throw new IllegalArgumentException("You can't create a user with a higher role than yourself!");
        }

        user = userService.createUser(user);
        UriComponents uriComponents = uriBuilder.path("/api/user/{id}").buildAndExpand(user.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @RequestMapping(value = {"{id}", "current"}, method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable(value = "id", required = false) Long userId, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ERole authority = principal.getAuthority();
        User updateUser = userService.fromDto(updateUserRequest.getUserDto());
        if(userId == null) {
            userId = principal.getId();
        } else {
            if(!principal.getAuthorities().contains(ERole.ROLE_ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        updateUser.setId(userId);
        User beforeUpdate = userService.getUser(userId);
        if(beforeUpdate == null) {
            return ResponseEntity.notFound().build();
        }
        if(beforeUpdate.getAuthority().getLevel() > authority.getLevel()) {
            throw new IllegalArgumentException("You can't edit a user with a higher role than yourself!");
        }
        if(principal.getAuthorities().contains(ERole.ROLE_ADMIN)) {
            if(principal.getId() == userId) {
                if(updateUser.getAuthority() != beforeUpdate.getAuthority()) {
                    throw new IllegalArgumentException("You can't edit your own role!");
                }
                if(updateUser.isAccountNonLocked() != beforeUpdate.isAccountNonLocked()) {
                    throw new IllegalArgumentException("You can't lock / unlock yourself!");
                }
            }
        } else {
            updateUser.setAuthority(beforeUpdate.getAuthority());
            updateUser.setAccountNonLocked(beforeUpdate.isAccountNonLocked());
        }
        //If user wants to update his password update it. Otherwise fill in the old encrypted password
        if(!updateUserRequest.isUpdatePassword()) {
            updateUser.setPassword(beforeUpdate.getPassword());
        }
        return ResponseEntity.ok(new UserDto.Response.Detailed(userService.updateUser(updateUser, updateUserRequest.isUpdatePassword())));
    }



    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUser(@PathVariable("id") long userId) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal.getId() == userId) {
            throw new IllegalArgumentException("You can't delete yourself!");
        }
        User beforeDelete = userService.getUser(userId);
        if(beforeDelete == null) {
            return ResponseEntity.notFound().build();
        }
        if (beforeDelete.getAuthority().getLevel() > principal.getAuthority().getLevel()) {
            throw new IllegalArgumentException("You can't delete a user with a higher role than yourself!");
        }
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = {"{id}", "current"}, method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable(value = "id", required = false) Long userId) {
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
        return ResponseEntity.ok(new UserDto.Response.Detailed(user));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getUsers() {
        List<UserDto.Response.Detailed> userDtoList = userService.getUsers()
                .stream().map(UserDto.Response.Detailed::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtoList);
    }
}
