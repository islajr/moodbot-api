package org.project.moodbotbackend.dto.reset;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PasswordResetDTO(

        @NotNull(message = "password field is required")
        @Size(min = 8, max = 75)
        String password
) {
}
