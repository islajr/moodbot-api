package org.project.moodbotbackend.dto.app;

import lombok.Builder;

@Builder
public record AiRequestDTO(
        String user_id,
        String message
) {
}
