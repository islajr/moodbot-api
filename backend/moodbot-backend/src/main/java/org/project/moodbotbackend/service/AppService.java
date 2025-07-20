package org.project.moodbotbackend.service;

import java.util.ArrayList;

import org.project.moodbotbackend.dto.app.AppResponse;
import org.project.moodbotbackend.entity.Chat;
import org.project.moodbotbackend.entity.User;
import org.project.moodbotbackend.entity.UserPrincipal;
import org.project.moodbotbackend.repository.AuthRepository;
import org.project.moodbotbackend.repository.ChatRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppService {

    private final MyUserDetailsService myUserDetailsService;
    private final AuthRepository authRepository;
    private final ChatRepository chatRepository;

    public ResponseEntity<AppResponse> generateMainPage() {

        // obtain email from securityContextHolder
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();
        String email = ((UserPrincipal) myUserDetailsService.loadUserByUsername(identifier)).getEmail();

        User user = authRepository.findUserByEmail(email);
        ArrayList<Chat> chats = chatRepository.findChatsByUser(user);

        return ResponseEntity.ok(AppResponse.builder()
                .chats(chats)
                .build());
    }

    public ResponseEntity<Chat> getChat(String sessionId) {
        Chat chat =  chatRepository.findChatBySessionId(sessionId);
        if (chat == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(chat);
    }
}
