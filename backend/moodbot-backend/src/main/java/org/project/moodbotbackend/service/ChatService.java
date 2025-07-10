package org.project.moodbotbackend.service;

import lombok.RequiredArgsConstructor;
import org.project.moodbotbackend.dto.app.ChatMessageDTO;
import org.project.moodbotbackend.entity.Chat;
import org.project.moodbotbackend.entity.ChatMessage;
import org.project.moodbotbackend.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public void saveMessage(ChatMessageDTO messageDTO, String sessionId) {
        ChatMessage message  = ChatMessage.builder()
                .content(messageDTO.content())
                .sender(messageDTO.sender())
                .sessionId(sessionId)
                .userId(messageDTO.userId())
                .timestamp(LocalDateTime.now())
                .build();

        chatRepository.save(message);

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
