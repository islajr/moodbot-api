package org.project.moodbotbackend.service;

import org.project.moodbotbackend.entity.Chat;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatService {


    public Chat generateResponse(Chat chat) {
        Chat response = new Chat();

        response.setContent("");    // query ai section
        response.setSender("");     // get from auth?
        response.setCreatedAt(LocalDateTime.now());
        // persist response?

        return response;

    }
}
