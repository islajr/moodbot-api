package org.project.moodbotbackend.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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

    ArrayList<ChatMessage> messages;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }
}
