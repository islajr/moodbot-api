package org.project.moodbotbackend.dto.auth;

public record VerificationDTO(
        String email,
        int code
) {
}
