package org.project.moodbotbackend.dto.reset.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsernameResetDTO(

        @NotNull(message = "username field is required.")
        @Size(min = 1, max = 20)
        String username
) {
}
