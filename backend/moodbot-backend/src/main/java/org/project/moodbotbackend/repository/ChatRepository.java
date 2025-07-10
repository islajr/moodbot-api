package org.project.moodbotbackend.repository;

import org.project.moodbotbackend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

}
