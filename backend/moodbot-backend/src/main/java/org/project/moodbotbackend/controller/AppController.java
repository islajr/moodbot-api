package org.project.moodbotbackend.controller;

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
public class AppController {

    private final AppService appService;

    @GetMapping
    public ResponseEntity<MainAppResponse> getMainPage() {
        return appService.generateMainPage();
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ChatResponse> getChat(@PathVariable String sessionId) {
        return appService.getChat(sessionId);
    }
}
