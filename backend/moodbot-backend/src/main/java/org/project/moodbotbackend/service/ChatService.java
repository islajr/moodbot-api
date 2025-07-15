package org.project.moodbotbackend.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.moodbotbackend.dto.app.ChatMessageDTO;
import org.project.moodbotbackend.entity.Chat;
import org.project.moodbotbackend.entity.ChatMessage;
import org.project.moodbotbackend.entity.User;
import org.project.moodbotbackend.entity.UserPrincipal;
import org.project.moodbotbackend.repository.AuthRepository;
import org.project.moodbotbackend.repository.ChatRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final AuthRepository authRepository;
    private final MyUserDetailsService myUserDetailsService;

    public Cache<String, Chat> newChats = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(24))
            .build();   // probably handle this flow better later on.

    public void saveMessage(ChatMessageDTO messageDTO, String sessionId, Principal user) {
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();
        String email = ((UserPrincipal) myUserDetailsService.loadUserByUsername(identifier)).getEmail();

        User myUser = authRepository.findUserByEmail(email);

        ChatMessage message  = ChatMessage.builder()
                .content(messageDTO.content())
                .sender(user.getName())
                .sessionId(sessionId)
                .userId(messageDTO.userId())
                .timestamp(LocalDateTime.now())
                .build();

        // if it's a new chat...
        if (newChats.getIfPresent(myUser.getUsername()) != null) {
          Chat chat = newChats.getIfPresent(myUser.getUsername());
          newChats.invalidate(myUser.getUsername());    // delete entry in cache, ensuring there is only one available.
            if (chat != null) {     // obligatory error check
                chat.addMessage(message);   // add message to message list
                chatRepository.save(chat);  // persist chat in memory
                log.info("saved message");
            }
            // throw exception (maybe, lol)
        }

        // otherwise...
        Chat chat = chatRepository.findChatBySessionId(sessionId);
        chat.addMessage(message);
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
