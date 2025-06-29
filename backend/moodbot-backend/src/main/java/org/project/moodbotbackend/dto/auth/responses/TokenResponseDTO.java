package org.project.moodbotbackend.dto.auth.responses;

public record TokenResponseDTO(
        String accessToken,
        String refreshToken
) {
}
