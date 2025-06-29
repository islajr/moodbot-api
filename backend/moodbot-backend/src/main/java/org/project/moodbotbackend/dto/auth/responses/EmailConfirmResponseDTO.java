package org.project.moodbotbackend.dto.auth.responses;

public record EmailConfirmResponseDTO(
        String message,
        String email
) {
}
