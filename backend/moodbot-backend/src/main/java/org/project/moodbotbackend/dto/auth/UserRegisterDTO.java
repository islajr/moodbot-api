package org.project.moodbotbackend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.project.moodbotbackend.entity.User;

public record UserRegisterDTO(

        @NotNull
        @NotBlank(message = "a username is required")
        @Size(min = 1, max = 20)
        String username,

        @NotNull
        @NotBlank(message = "an e-mail address is required")
        @Email(message = "provide a valid e-mail address")
        @Size(min = 5, max = 50)
        String email,

        @NotNull
        @NotBlank(message = "a password is required")
        @Size(min = 8, max = 75)
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
