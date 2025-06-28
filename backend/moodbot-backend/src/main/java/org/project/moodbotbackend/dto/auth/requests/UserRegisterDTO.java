package org.project.moodbotbackend.dto.auth.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.project.moodbotbackend.entity.User;

public record UserRegisterDTO(

        @NotNull
        @NotBlank(message = "a username is required")
        @Size(min = 1, max = 20, message = "username can only be between 1 and 20 characters long")
        String username,

        @NotNull
        @NotBlank(message = "an e-mail address is required")
        @Email(message = "provide a valid e-mail address")
        @Size(min = 5, max = 50, message = "email must be between 5 and 50 characters long")
        String email,

        @NotNull
        @NotBlank(message = "a password is required")
        @Size(min = 8, max = 75, message = "password must be between 8 and 75 characters long")
        String password
) {

    public static User toUser(UserRegisterDTO userRegisterDTO) {
        User user = new User();
        user.setUsername(userRegisterDTO.username);
        user.setEmail(userRegisterDTO.email);
        user.setPassword(userRegisterDTO.password);
        return user;
    }
}
