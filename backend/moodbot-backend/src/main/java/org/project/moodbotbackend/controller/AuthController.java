package org.project.moodbotbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.project.moodbotbackend.dto.auth.requests.ConfirmationDTO;
import org.project.moodbotbackend.dto.auth.requests.UserLoginDTO;
import org.project.moodbotbackend.dto.auth.requests.UserRegisterDTO;
import org.project.moodbotbackend.dto.auth.requests.VerificationDTO;
import org.project.moodbotbackend.dto.auth.responses.EmailConfirmResponseDTO;
import org.project.moodbotbackend.dto.auth.responses.TokenResponseDTO;
import org.project.moodbotbackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/moodbot/auth")
@Tag(name = "Authentication", description = "This documentation explains how authentication has been implemented within this API.")
public class AuthController {

    private final AuthService authService;

    @Operation(description = "This endpoint registers new users.")
    @PostMapping("/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created the user and sent confirmation e-mail"),
            @ApiResponse(responseCode = "500", description = "Error creating user or sending confirmation e-mail")
    })
    public ResponseEntity<EmailConfirmResponseDTO> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        return authService.registerUser(userRegisterDTO);
    }

    @Operation(description = "This endpoint logs users into the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> loginUser(@RequestBody UserLoginDTO userLoginDTO) {
        return authService.loginUser(userLoginDTO);
    }

    @Operation(description = "This endpoint refreshes the access token", summary = "Authentication is required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token Successfully refreshed"),
            @ApiResponse(responseCode = "400", description = "Invalid Token")
    })

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refreshToken() {
        return authService.refreshToken();
    }

    @Operation(description = "This endpoint confirms a OTP code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Incorrect code")
    })
    @PostMapping("/confirm")
    public ResponseEntity<TokenResponseDTO> confirmAction(@RequestBody VerificationDTO verificationDTO) {
        return authService.confirm(verificationDTO.email(), verificationDTO.code());
    }

    @Operation(description = "This endpoint can service the 'forgot password' feature, or prompt unverified users to verify their e-mail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated OTP"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Error sending OTP")
    })
    @PostMapping("/verify")
    public ResponseEntity<EmailConfirmResponseDTO> verifyEmail(@RequestBody ConfirmationDTO confirmationDTO) {
        return authService.verify(confirmationDTO.email());
    }

}
