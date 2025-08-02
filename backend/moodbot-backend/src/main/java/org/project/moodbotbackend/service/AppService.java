package org.project.moodbotbackend.service;

import lombok.RequiredArgsConstructor;
import org.project.moodbotbackend.dto.app.ChatMessageDTO;
import org.project.moodbotbackend.dto.app.ChatResponse;
import org.project.moodbotbackend.dto.app.MainAppResponse;
import org.project.moodbotbackend.entity.Chat;
import org.project.moodbotbackend.entity.ChatMessage;
import org.project.moodbotbackend.entity.User;
import org.project.moodbotbackend.entity.UserPrincipal;
import org.project.moodbotbackend.exceptions.auth.AuthException;
import org.project.moodbotbackend.repository.AuthRepository;
import org.project.moodbotbackend.repository.ChatMessageRepository;
import org.project.moodbotbackend.repository.ChatRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppService {

    private final MyUserDetailsService myUserDetailsService;
    private final AuthRepository authRepository;
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ResponseEntity<MainAppResponse> generateMainPage() {

        // obtain email from securityContextHolder
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();
        String email = ((UserPrincipal) myUserDetailsService.loadUserByUsername(identifier)).getEmail();

        User user = authRepository.findUserByEmail(email);
        ArrayList<Chat> chats = chatRepository.findChatsByUser(user);

        if (chats == null || chats.isEmpty()) {
            throw new AuthException(404, "there are no chats to display");
        }

       List<ChatResponse> chatResponses = new ArrayList<>();
       List<ChatMessageDTO> chatMessageDTOs = new ArrayList<>();
        for (Chat chat : chats) {
            // compute the chat message dto first

            for (ChatMessage chatMessage : chat.getMessages()) {
                chatMessageDTOs.add(ChatMessageDTO.builder()
                                .userId(chatMessage.getUserId())
                                .sessionId(chatMessage.getSessionId())
                                .content(chatMessage.getContent())
                                .sender(chatMessage.getSender())
                                .timestamp(chatMessage.getTimestamp())
                        .build());
            }

            chatResponses.add(ChatResponse.builder()
                            .slug(chat.getSlug())
                            .sessionId(chat.getSessionId())
                            .messages(chatMessageDTOs)
                            .updatedAt(chat.getUpdatedAt())
                    .build());
        }


        return ResponseEntity.ok(MainAppResponse.builder()
                        .chat(chatResponses)
                .build()
        );
    }

    public ResponseEntity<ChatResponse> getChat(String sessionId) {
        Chat chat =  chatRepository.findChatBySessionId(sessionId);
        List<ChatMessage> chatMessages = chatMessageRepository.findChatMessagesBySessionId(chat.getSessionId());

        List<ChatMessageDTO> chatMessageDTOList = new ArrayList<>();
        if (!chatMessages.isEmpty()) {
            for (ChatMessage chatMessage : chatMessages) {
                chatMessageDTOList.add(ChatMessageDTO.builder()
                                .userId(chatMessage.getUserId())
                                .sessionId(chatMessage.getSessionId())
                                .content(chatMessage.getContent())
                                .sender(chatMessage.getSender())
                                .timestamp(chatMessage.getTimestamp())
                        .build());
            }

            return ResponseEntity.ok(ChatResponse.builder()
                            .slug(chat.getSlug())
                            .messages(chatMessageDTOList)
                            .sessionId(chat.getSessionId())
                            .updatedAt(chat.getUpdatedAt())
                    .build());

        } return ResponseEntity.ok(ChatResponse.builder()
                        .slug(chat.getSlug())
                        .messages(new ArrayList<>())
                        .sessionId(chat.getSessionId())
                        .updatedAt(chat.getUpdatedAt())
                .build());
    }
}
