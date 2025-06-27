package org.project.moodbotbackend.controller;

import lombok.AllArgsConstructor;
import org.project.moodbotbackend.dto.reset.EmailResetDTO;
import org.project.moodbotbackend.dto.reset.PasswordResetDTO;
import org.project.moodbotbackend.dto.reset.ResetDTO;
import org.project.moodbotbackend.dto.reset.UsernameResetDTO;
import org.project.moodbotbackend.service.ResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/moodbot/reset")
@RestController
@AllArgsConstructor
public class ResetController {

    private final ResetService resetService;

    @PutMapping("/password")
    public ResponseEntity<String> passwordReset(@RequestBody PasswordResetDTO passwordResetDTO) {
        return resetService.passwordReset(passwordResetDTO);
    }

    @PutMapping("/username")
    public ResponseEntity<String> usernameReset(@RequestBody UsernameResetDTO usernameResetDTO) {
        return resetService.usernameReset(usernameResetDTO);
    }

    @PutMapping("/email")
    public ResponseEntity<String> emailReset(@RequestBody EmailResetDTO emailResetDTO) {
        return resetService.emailReset(emailResetDTO);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPassword(@RequestParam String action, @RequestBody ResetDTO resetDTO) {
        return resetService.verify(action, resetDTO.code());
    }
}
