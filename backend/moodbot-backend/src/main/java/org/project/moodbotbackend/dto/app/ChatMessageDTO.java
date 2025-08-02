package org.project.moodbotbackend.dto.app;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessageDTO(
        String sessionId,
        Long userId,
        String sender,
        String content,
        LocalDateTime timestamp
) {}
