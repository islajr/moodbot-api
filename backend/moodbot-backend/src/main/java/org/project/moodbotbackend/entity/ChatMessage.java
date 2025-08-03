package org.project.moodbotbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String sessionId;

    @ManyToOne
    private Chat chat;
    private String sender;
    private String content;

    LocalDateTime timestamp;
}
