package org.project.moodbotbackend.service;

import java.security.Principal;
import java.time.LocalDateTime;

import org.project.moodbotbackend.dto.app.AiRequestDTO;
import org.project.moodbotbackend.dto.app.AiResponseDTO;
import org.project.moodbotbackend.dto.app.ChatMessageDTO;
import org.project.moodbotbackend.entity.Chat;
import org.project.moodbotbackend.entity.ChatMessage;
import org.project.moodbotbackend.entity.User;
import org.project.moodbotbackend.entity.UserPrincipal;
import org.project.moodbotbackend.exceptions.auth.AuthException;
import org.project.moodbotbackend.repository.AuthRepository;
import org.project.moodbotbackend.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final AuthRepository authRepository;
    private final MyUserDetailsService myUserDetailsService;
    private final RestTemplate restTemplate;

    @Value("${moodbot.ai.service.url}")
    private String aiServiceURL;

    public void saveMessage(ChatMessageDTO messageDTO, String sessionId, Principal user) {
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(identifier);
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

    public ChatMessageDTO generateResponse(ChatMessageDTO chatMessageDTO, Principal user) {

        Chat chat = chatRepository.findChatBySessionId(chatMessageDTO.sessionId());

        // preparing the request
        User savedUser = authRepository.findUserByUsername(user.getName());

        AiRequestDTO request = AiRequestDTO.builder()
                .user_id(savedUser.getId().toString())
                .message(chatMessageDTO.content())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AiRequestDTO> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<AiResponseDTO> response = restTemplate.exchange(
                aiServiceURL,
                HttpMethod.POST,
                httpEntity,
                AiResponseDTO.class

        );

        AiResponseDTO responseDTO = response.getBody();

        // convert ai response to chat message for persisting
        if (responseDTO != null) {

            ChatMessage chatMessage = ChatMessage.builder()
                    .content(responseDTO.response())
                    .sender("BOT")
                    .sessionId(chatMessageDTO.sessionId())
                    .userId(null)
                    .timestamp(LocalDateTime.now())
                    .build();

            chat.addMessage(chatMessage);
            if (chat.getSlug().equals("default-slug"))
                    chat.setSlug(responseDTO.slug());

            chat.setUpdatedAt(LocalDateTime.now());
            chatRepository.save(chat);
            return toDto(chatMessage);
        }
        throw new AuthException(502, "Bad Gateway");

    }
}
