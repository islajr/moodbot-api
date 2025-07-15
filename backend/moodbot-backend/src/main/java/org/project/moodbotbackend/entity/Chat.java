package org.project.moodbotbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    String sessionId;
    String slug;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    ArrayList<ChatMessage> messages = new ArrayList<>();
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }
}
