package org.project.moodbotbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class User {

    @Id
    private Long id;
    String username;
    String email;
    private String password;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}
