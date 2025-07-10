package org.project.moodbotbackend.controller;

import lombok.AllArgsConstructor;
import org.project.moodbotbackend.dto.app.ChatMessageDTO;
import org.project.moodbotbackend.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/{sessionId}")
    @SendTo("/topic/messages/{sessionId}")
    public ChatMessageDTO respond(@DestinationVariable String sessionId, @Payload ChatMessageDTO chatMessageDTO) {

        // save user message
        chatService.saveMessage(chatMessageDTO, sessionId);

        // generate response

        // save response

        // return response
        return null;
    }
}
