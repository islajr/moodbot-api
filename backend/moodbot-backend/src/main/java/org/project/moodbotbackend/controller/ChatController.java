package org.project.moodbotbackend.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.project.moodbotbackend.dto.app.ChatMessageDTO;
import org.project.moodbotbackend.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/{sessionId}")
    @SendTo("/topic/messages/{sessionId}")
    public ChatMessageDTO respond(@DestinationVariable String sessionId, @Payload ChatMessageDTO chatMessageDTO, Principal user) {

        // save user message
        // chatService.saveMessage(chatMessageDTO, sessionId, user);

        // generate response

        // save response

        // return response
        // return null;
        return new ChatMessageDTO(
                sessionId,
                1L,
                "BOT",
                "this is a bare response",
                LocalDateTime.now()
        );
    }
}
