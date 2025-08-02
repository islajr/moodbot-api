package org.project.moodbotbackend.dto.app;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ChatResponse(
        String sessionId,
        String slug,
        LocalDateTime updatedAt,
        List<ChatMessageDTO> messages

) {
}
