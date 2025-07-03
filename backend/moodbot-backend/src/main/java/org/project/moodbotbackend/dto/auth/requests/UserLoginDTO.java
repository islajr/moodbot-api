package org.project.moodbotbackend.dto.auth.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserLoginDTO(

        @NotBlank(message = "input your email or username")
        @NotNull
        String identifier,

        @NotNull
        @NotBlank(message = "input your password")
        String password
) {
}
