package org.project.moodbotbackend.dto.app;

import java.time.LocalDateTime;

public record ChatMessageDTO(
        String sessionId,
        Long userId,
        String sender,
        String content,
        LocalDateTime timestamp
) {}
