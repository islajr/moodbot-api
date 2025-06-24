package org.project.moodbotbackend.service;

import lombok.AllArgsConstructor;
import org.project.moodbotbackend.dto.auth.UserLoginDTO;
import org.project.moodbotbackend.dto.auth.UserRegisterDTO;
import org.project.moodbotbackend.entity.User;
import org.project.moodbotbackend.entity.UserPrincipal;
import org.project.moodbotbackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;

    public ResponseEntity<String> registerUser(UserRegisterDTO userRegisterDTO) {
        User user = UserRegisterDTO.toUser(userRegisterDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());


        userRepository.save(user);
        return ResponseEntity.ok("""
                access token: %s
                
                refresh token: %s
                """.formatted(jwtService.generateToken(user.getEmail()), jwtService.generateRefreshToken(user.getEmail())));
    }

    public ResponseEntity<String> loginUser(UserLoginDTO userLoginDTO) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.identifier(), userLoginDTO.password()));

        if (authentication.isAuthenticated()) {
            String email = ((UserPrincipal) myUserDetailsService.loadUserByUsername(userLoginDTO.identifier())).getEmail();
            return ResponseEntity.ok("""
                    access token: %s
                    
                    refresh token: %s
                    """.formatted(jwtService.generateToken(email), jwtService.generateRefreshToken(email)));
        }
        throw new BadCredentialsException("incorrect details");
    }

    public ResponseEntity<String> refreshToken() {
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();
        String email = ((UserPrincipal) myUserDetailsService.loadUserByUsername(identifier)).getEmail();

        return ResponseEntity.ok("""
                access token: %s
                
                refresh token: %s
                """.formatted(jwtService.generateToken(email), jwtService.generateRefreshToken(email)));
    }
}
