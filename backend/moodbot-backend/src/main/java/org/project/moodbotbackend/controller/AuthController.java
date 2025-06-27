package org.project.moodbotbackend.controller;

import lombok.AllArgsConstructor;
import org.project.moodbotbackend.dto.auth.UserLoginDTO;
import org.project.moodbotbackend.dto.auth.UserRegisterDTO;
import org.project.moodbotbackend.dto.auth.VerificationDTO;
import org.project.moodbotbackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/moodbot/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        return authService.registerUser(userRegisterDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserLoginDTO userLoginDTO) {
        return authService.loginUser(userLoginDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken() {
        return authService.refreshToken();
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyAction(@RequestBody VerificationDTO verificationDTO) {
        return authService.verify(verificationDTO.email(), verificationDTO.code());
    }

}
