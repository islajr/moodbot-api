package org.project.moodbotbackend.dto.reset;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmailResetDTO(

        @NotNull(message = "e-mail field is required.")
        @Email(message = "please input a valid e-mail address.")
        @Size(min = 5, max = 50)
        String email
) {
}
