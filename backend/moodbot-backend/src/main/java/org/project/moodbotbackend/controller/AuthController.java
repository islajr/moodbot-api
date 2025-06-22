package org.project.moodbotbackend.controller;

import lombok.AllArgsConstructor;
import org.project.moodbotbackend.dto.auth.UserLoginDTO;
import org.project.moodbotbackend.dto.auth.UserRegisterDTO;
import org.project.moodbotbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/moodbot/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.registerUser(userRegisterDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.loginUser(userLoginDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken() {
        return userService.refreshToken();
    }

}
