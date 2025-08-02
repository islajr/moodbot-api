package org.project.moodbotbackend.dto.app;

import lombok.Builder;

import java.util.List;

@Builder
public record MainAppResponse(
    List<ChatResponse> chat
) {
}
