package org.project.moodbotbackend.service;

import java.security.Principal;
import java.time.LocalDateTime;

import org.project.moodbotbackend.dto.app.ChatMessageDTO;
import org.project.moodbotbackend.entity.Chat;
import org.project.moodbotbackend.entity.ChatMessage;
import org.project.moodbotbackend.entity.User;
import org.project.moodbotbackend.entity.UserPrincipal;
import org.project.moodbotbackend.repository.AuthRepository;
import org.project.moodbotbackend.repository.ChatRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final AuthRepository authRepository;
    private final MyUserDetailsService myUserDetailsService;

    public void saveMessage(ChatMessageDTO messageDTO, String sessionId, Principal user) {
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();
        String email = ((UserPrincipal) myUserDetailsService.loadUserByUsername(identifier)).getEmail();

        User myUser = authRepository.findUserByEmail(email);

        ChatMessage message  = ChatMessage.builder()
                .content(messageDTO.content())
                .sender(user.getName())
                .sessionId(sessionId)
                // .userId(messageDTO.userId())
                .userId(myUser.getId())
                .timestamp(LocalDateTime.now())
                .build();

    
        Chat chat = chatRepository.findChatBySessionId(sessionId);

        if (chat != null) { // if chat exists
            chat.addMessage(message);
            log.info("chat found, adding message");
            chat.setUpdatedAt(LocalDateTime.now());
            chatRepository.save(chat);
            log.info("chat saved");
        } else {
            log.info("chat not found, creating new chat");
            chat = Chat.builder()
                    .sessionId(sessionId)
                    .slug("default-slug") // might want to change this later on
                    .user(myUser)
                    .messages(null)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(null)
                    .build();
        }
        chat.addMessage(message);
        chat.setUpdatedAt(LocalDateTime.now());
        log.info("adding message to chat");
        chatRepository.save(chat);
        log.info("saved chat");

    }

    private ChatMessageDTO toDto(ChatMessage message) {
        return new ChatMessageDTO(
               message.getSessionId(),
               message.getUserId(),
               message.getSender(),
               message.getContent(),
               message.getTimestamp()
        );
    }
}
