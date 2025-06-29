package org.project.moodbotbackend.dto.auth.requests;

public record ConfirmationDTO(
        String email,
        String code
) {
}
