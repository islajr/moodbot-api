package org.project.moodbotbackend.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import org.project.moodbotbackend.dto.auth.requests.UserLoginDTO;
import org.project.moodbotbackend.dto.auth.requests.UserRegisterDTO;
import org.project.moodbotbackend.dto.auth.responses.EmailConfirmResponseDTO;
import org.project.moodbotbackend.dto.auth.responses.TokenResponseDTO;
import org.project.moodbotbackend.entity.EmailDetails;
import org.project.moodbotbackend.entity.User;
import org.project.moodbotbackend.entity.UserPrincipal;
import org.project.moodbotbackend.exceptions.auth.AuthException;
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

    public ResponseEntity<EmailConfirmResponseDTO> registerUser(UserRegisterDTO userRegisterDTO) {
        User user = UserRegisterDTO.toUser(userRegisterDTO);
        if (authRepository.existsByUsername(user.getUsername()))
            throw new AuthException(409, "username is taken!");
        if (authRepository.existsByEmail(user.getEmail()))
            throw new AuthException(409, "e-mail is taken!");

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

        return ResponseEntity.status(HttpStatus.CREATED).body(new EmailConfirmResponseDTO("registration successful", user.getEmail()));
    }

    public ResponseEntity<TokenResponseDTO> loginUser(UserLoginDTO userLoginDTO) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.identifier(), userLoginDTO.password()));
            String email = ((UserPrincipal) myUserDetailsService.loadUserByUsername(userLoginDTO.identifier())).getEmail();
            String username = myUserDetailsService.loadUserByUsername(userLoginDTO.identifier()).getUsername();


        if (email != null) {
                User user = authRepository.findUserByEmail(email);

                if (user != null && user.isEmailVerified()) {
                    if (authentication.isAuthenticated()) {
                        return generateToken(email);
                    }
                    throw new AuthException(400, "incorrect details");
                }

        } throw new AuthException(404, "no such user.");


    }

    public ResponseEntity<TokenResponseDTO> refreshToken() {
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();
        String email = ((UserPrincipal) myUserDetailsService.loadUserByUsername(identifier)).getEmail();

        return generateToken(email);
    }

    public ResponseEntity<TokenResponseDTO> confirm(String email, int code) {
        // scan cache for username;
        Integer verificationCode = tempCache.getIfPresent(email);

        if (verificationCode != null) {
            if (verificationCode != code) {
                throw new AuthException(400, "incorrect code.");
            }
        } else {
            throw new AuthException(400, "please try again");
        }

        // tempCache.invalidate(email);
        User user = authRepository.findUserByEmail(email);
        user.setEmailVerified(true);
        authRepository.save(user);
        return generateToken(email);    // generate JWT
    }

    public ResponseEntity<EmailConfirmResponseDTO> verify(String email) {

        tempCache.invalidate(email);
        String username = myUserDetailsService.loadUserByUsername(email).getUsername();

        int verificationCode = generateOTP();    // generate code.
        String body = generateBody(username, "register", verificationCode);

        EmailDetails emailDetails = new EmailDetails(
                email,
                body,
                "Account Creation Confirmation"
        );
        emailService.sendMail(emailDetails);

        // add to temp cache
        tempCache.put(email, verificationCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EmailConfirmResponseDTO(
                "a verification e-mail has been re-sent to your e-mail", email
        ));

    }



    private ResponseEntity<TokenResponseDTO> generateToken(String email) {
        return ResponseEntity.ok(new TokenResponseDTO(jwtService.generateToken(email), jwtService.generateRefreshToken(email)));
    }
}
