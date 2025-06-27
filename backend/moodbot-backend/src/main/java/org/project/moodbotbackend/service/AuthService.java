package org.project.moodbotbackend.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import org.project.moodbotbackend.dto.auth.UserLoginDTO;
import org.project.moodbotbackend.dto.auth.UserRegisterDTO;
import org.project.moodbotbackend.entity.EmailDetails;
import org.project.moodbotbackend.entity.User;
import org.project.moodbotbackend.entity.UserPrincipal;
import org.project.moodbotbackend.repository.AuthRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.project.moodbotbackend.util.EmailUtil.generateBody;
import static org.project.moodbotbackend.util.EmailUtil.generateOTP;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;
    private final EmailService emailService;

    private final Cache<String, Integer> tempCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(30))
            .maximumSize(1000)
            .build();

    public ResponseEntity<String> registerUser(UserRegisterDTO userRegisterDTO) {
        User user = UserRegisterDTO.toUser(userRegisterDTO);
        if (authRepository.existsByUsername(user.getUsername()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("username is taken!");
        if (authRepository.existsByEmail(user.getEmail()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("e-mail is taken!");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setEmailVerified(false);
        authRepository.save(user);

        int verificationCode = generateOTP();    // generate code.
        String body = generateBody(user.getUsername(), "register", verificationCode);

        EmailDetails emailDetails = new EmailDetails(
                user.getEmail(),
                body,
                "Account Creation Confirmation"
        );
        emailService.sendMail(emailDetails);

        // add to temp cache
        tempCache.put(user.getEmail(), verificationCode);

        return ResponseEntity.status(HttpStatus.CREATED).body("""
                registration successful!
                confirmation e-mail has been sent to %s
                """.formatted(user.getEmail()));


    }

    public ResponseEntity<String> loginUser(UserLoginDTO userLoginDTO) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.identifier(), userLoginDTO.password()));
            String email = ((UserPrincipal) myUserDetailsService.loadUserByUsername(userLoginDTO.identifier())).getEmail();

            if (email != null) {
                User user = authRepository.findUserByEmail(email);

                if (user != null && user.isEmailVerified()) {
                    if (authentication.isAuthenticated()) {
                        return generateToken(email);
                    }
                    throw new BadCredentialsException("incorrect details");
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("please verify your e-mail first.");
            } return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no such user.");


    }

    public ResponseEntity<String> refreshToken() {
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();
        String email = ((UserPrincipal) myUserDetailsService.loadUserByUsername(identifier)).getEmail();

        return generateToken(email);
    }

    public ResponseEntity<String> verify(String email, int code) {
        // scan cache for username;
        Integer verificationCode = tempCache.getIfPresent(email);

        if (verificationCode != null) {
            if (verificationCode != code) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("incorrect code.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("please try again");
        }

        // tempCache.invalidate(email);
        User user = authRepository.findUserByEmail(email);
        user.setEmailVerified(true);
        authRepository.save(user);
        return generateToken(email);    // generate JWT
    }

    private ResponseEntity<String> generateToken(String email) {
        return ResponseEntity.ok("""
                access token: %s
                
                refresh token: %s
                """.formatted(jwtService.generateToken(email), jwtService.generateRefreshToken(email)));
    }
}
