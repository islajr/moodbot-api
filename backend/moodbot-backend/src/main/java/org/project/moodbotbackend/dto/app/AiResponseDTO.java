package org.project.moodbotbackend.dto.app;

import lombok.Builder;

@Builder
public record AiResponseDTO(
        String response,
        String slug
) {
}
