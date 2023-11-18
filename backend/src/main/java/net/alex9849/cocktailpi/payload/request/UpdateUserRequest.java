package net.alex9849.cocktailpi.payload.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import net.alex9849.cocktailpi.payload.dto.user.UserDto;

public class UpdateUserRequest {

    private boolean updatePassword;

    @NotNull
    @Valid
    private UserDto.Request.Create userDto;

    public boolean isUpdatePassword() {
        return updatePassword;
    }

    public void setUpdatePassword(boolean updatePassword) {
        this.updatePassword = updatePassword;
    }

    public UserDto.Request.Create getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto.Request.Create userDto) {
        this.userDto = userDto;
    }
}
