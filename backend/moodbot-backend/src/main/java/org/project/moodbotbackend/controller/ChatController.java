package org.project.moodbotbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.project.moodbotbackend.dto.app.ChatMessageDTO;
import org.project.moodbotbackend.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@AllArgsConstructor
@Tag(name = "WebSocket Endpoints", description = "This documentation explains how to interact with the API using websockets")
public class ChatController {

    private final ChatService chatService;

    @Operation(description = "This endpoint receives websocket requests and sends them to the appropriate queues for processing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully responded"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "502", description = "Bad Gateway")

    })
    @MessageMapping("/chat/{sessionId}")
    @SendTo("/topic/messages/{sessionId}")
    public ChatMessageDTO respond(@DestinationVariable String sessionId, @Payload ChatMessageDTO chatMessageDTO, Principal user) {

        // save user message
         chatService.saveMessage(chatMessageDTO, sessionId, user);

        // generate response
        return chatService.generateResponse(chatMessageDTO, user);
    }
}
