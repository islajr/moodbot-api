package org.project.moodbotbackend.controller;

import lombok.AllArgsConstructor;
import org.project.moodbotbackend.dto.reset.requests.EmailResetDTO;
import org.project.moodbotbackend.dto.reset.requests.PasswordResetDTO;
import org.project.moodbotbackend.dto.reset.requests.ResetDTO;
import org.project.moodbotbackend.dto.reset.requests.UsernameResetDTO;
import org.project.moodbotbackend.dto.reset.responses.ResetResponseDTO;
import org.project.moodbotbackend.service.ResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/moodbot/reset")
@RestController
@AllArgsConstructor
public class ResetController {

    private final ResetService resetService;

    @PutMapping("/password")
    public ResponseEntity<ResetResponseDTO> passwordReset(@RequestBody PasswordResetDTO passwordResetDTO) {
        return resetService.passwordReset(passwordResetDTO);
    }

    @PutMapping("/username")
    public ResponseEntity<ResetResponseDTO> usernameReset(@RequestBody UsernameResetDTO usernameResetDTO) {
        return resetService.usernameReset(usernameResetDTO);
    }

    @PutMapping("/email")
    public ResponseEntity<ResetResponseDTO> emailReset(@RequestBody EmailResetDTO emailResetDTO) {
        return resetService.emailReset(emailResetDTO);
    }

    @PostMapping("/verify")
    public ResponseEntity<ResetResponseDTO> verifyPassword(@RequestParam String action, @RequestBody ResetDTO resetDTO) {
        return resetService.verify(action, resetDTO.code());
    }
}
