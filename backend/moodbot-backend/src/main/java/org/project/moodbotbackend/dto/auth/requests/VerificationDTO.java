package org.project.moodbotbackend.dto.auth.requests;

public record VerificationDTO(
        String email,
        int code
) {
}
