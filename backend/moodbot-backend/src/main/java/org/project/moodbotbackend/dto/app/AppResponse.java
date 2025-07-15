package org.project.moodbotbackend.dto.app;

import lombok.Builder;
import org.project.moodbotbackend.entity.Chat;

import java.util.ArrayList;

@Builder
public record AppResponse(
        ArrayList<Chat> chats
) {
}
