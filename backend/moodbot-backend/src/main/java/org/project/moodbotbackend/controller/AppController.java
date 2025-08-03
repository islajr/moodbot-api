package org.project.moodbotbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.moodbotbackend.dto.app.ChatResponse;
import org.project.moodbotbackend.dto.app.MainAppResponse;
import org.project.moodbotbackend.service.AppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/moodbot/app")
@RequiredArgsConstructor
@Tag(name = "Main Features", description = "This documentation explains how the main features within this API work.")
public class AppController {

    private final AppService appService;

    @GetMapping
    @Operation(description = "This endpoint returns all chats associated with the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the user's chats"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    public ResponseEntity<MainAppResponse> getMainPage() {
        return appService.generateMainPage();
    }

    @Operation(description = "This endpoint returns the details contained in a requested chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the chat"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/{sessionId}")
    public ResponseEntity<ChatResponse> getChat(@PathVariable String sessionId) {
        return appService.getChat(sessionId);
    }
}
