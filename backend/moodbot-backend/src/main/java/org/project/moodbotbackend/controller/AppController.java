package org.project.moodbotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.project.moodbotbackend.dto.app.AppResponse;
import org.project.moodbotbackend.service.AppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/moodbot/app")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    @GetMapping
    public AppResponse getMainPage() {
        return appService.generateMainPage();
    }
}
