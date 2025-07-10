package org.project.moodbotbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    ArrayList<ChatMessage> messages = new ArrayList<>();
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
