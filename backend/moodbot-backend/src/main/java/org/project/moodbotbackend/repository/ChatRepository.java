package org.project.moodbotbackend.repository;

import org.project.moodbotbackend.entity.Chat;
import org.project.moodbotbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {

    ArrayList<Chat> findChatsByUser(User user);
    Chat findChatBySessionId(String sessionId);
}
