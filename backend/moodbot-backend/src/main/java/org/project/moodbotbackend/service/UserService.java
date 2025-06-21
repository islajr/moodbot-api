package org.project.moodbotbackend.service;

import lombok.AllArgsConstructor;
import org.project.moodbotbackend.dto.auth.UserLoginDTO;
import org.project.moodbotbackend.dto.auth.UserRegisterDTO;
import org.project.moodbotbackend.entity.User;
import org.project.moodbotbackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<String> registerUser(UserRegisterDTO userRegisterDTO) {
        User user = UserRegisterDTO.toUser(userRegisterDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        return ResponseEntity.ok("");
    }

    public ResponseEntity<String> loginUser(UserLoginDTO userLoginDTO) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.identifier(), userLoginDTO.password()));

        if (authentication.isAuthenticated()) {
            return ResponseEntity.ok("");
        }
        throw new BadCredentialsException("incorrect details");
    }
}
