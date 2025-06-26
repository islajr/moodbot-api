package org.project.moodbotbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "username")
    @Size(min = 1, max = 20)
    String username;

    @NotNull
    @Column(name = "email")
    @Size(min = 5, max = 50)
    String email;

    @NotNull
    @Column(name = "password")
    @Size(min = 8, max = 75)
    private String password;

    @NotNull
    @Column(name = "is_email_verified")
    boolean isEmailVerified;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}
