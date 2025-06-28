package org.project.moodbotbackend.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import org.project.moodbotbackend.dto.reset.requests.EmailResetDTO;
import org.project.moodbotbackend.dto.reset.requests.PasswordResetDTO;
import org.project.moodbotbackend.dto.reset.requests.UsernameResetDTO;
import org.project.moodbotbackend.entity.EmailDetails;
import org.project.moodbotbackend.entity.User;
import org.project.moodbotbackend.entity.UserPrincipal;
import org.project.moodbotbackend.repository.AuthRepository;
import org.project.moodbotbackend.util.EmailUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class ResetService {
    private final AuthRepository authRepository;
    private final EmailService emailService;
    private final Cache<String, Integer> otpCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(30))
            .maximumSize(1000)
            .build();

    private final Cache<String, String> passwordCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(30))
            .maximumSize(1000)
            .build();

    private final Cache<String, String> emailCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(30))
            .maximumSize(1000)
            .build();
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<String> passwordReset(PasswordResetDTO passwordResetDTO) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userPrincipal.getEmail();
        User user = authRepository.findUserByEmail(email);
        int code = EmailUtil.generateOTP();
        String body = EmailUtil.generateBody(user.getUsername(), "passwordReset", code);
        EmailDetails emailDetails = new EmailDetails(email, body, "Password Reset Confirmation");
        emailService.sendMail(emailDetails);

        otpCache.put(email, code);
        passwordCache.put(email, passwordResetDTO.password());

        return ResponseEntity.ok("confirmation email sent. please check your mail");
    }

    public ResponseEntity<String> usernameReset(UsernameResetDTO usernameResetDTO) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userPrincipal.getEmail();
        if (authRepository.existsByUsername(usernameResetDTO.username()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("username already exists.");
        User user = authRepository.findUserByEmail(email);

        if (user != null) {
            user.setUsername(usernameResetDTO.username());
            user.setUpdatedAt(LocalDateTime.now());
            authRepository.save(user);

            return ResponseEntity.ok("successfully updated username");
        } return ResponseEntity.status(HttpStatus.NOT_FOUND).body("unable to update username");
    }

    public ResponseEntity<String> emailReset(EmailResetDTO emailResetDTO) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userPrincipal.getEmail();
        User user =authRepository.findUserByEmail(email);
        int code = EmailUtil.generateOTP();
        String body = EmailUtil.generateBody(user.getUsername(), "emailReset", code);
        EmailDetails emailDetails = new EmailDetails(emailResetDTO.email(), body, "Email Reset Confirmation");
        emailService.sendMail(emailDetails);

        otpCache.put(email, code);
        emailCache.put(email, emailResetDTO.email());

        return ResponseEntity.ok("confirmation email sent. please check your mail");
    }

    public ResponseEntity<String> verify(String action, int code) {

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userPrincipal.getEmail();

        if (action.equals("password")) {
            String newPassword = passwordCache.getIfPresent(email);
            passwordCache.invalidate(email);    // remove from cache
            Integer otp = otpCache.getIfPresent(email);
            otpCache.invalidate(email); // remove from cache
            if (otp != null) {
                if (otp != code) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("incorrect code.");
                }
                if (newPassword != null) {
                    User user = authRepository.findUserByEmail(email);
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setCreatedAt(LocalDateTime.now());
                    authRepository.save(user);
                    return ResponseEntity.status(HttpStatus.OK).body("password successfully updated!");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("expired code. please try again");
            }

        } else if (action.equals("email")) {
            String newEmail = emailCache.getIfPresent(email);
            Integer otp = otpCache.getIfPresent(email);

            if (otp != null) {
                if (otp != code) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("incorrect code.");
                }
                if (newEmail != null) {
                    User user = authRepository.findUserByEmail(email);
                    if (user != null) {
                        user.setEmail(newEmail);
                        user.setEmailVerified(true);
                        user.setUpdatedAt(LocalDateTime.now());
                        authRepository.save(user);
                        return ResponseEntity.ok("e-mail successfully updated!");
                    }
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("expired code. please try again");
            }



        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("unexpected action.");
        }

        return null;
    }
}
