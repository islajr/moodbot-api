package org.project.moodbotbackend.service;

import lombok.RequiredArgsConstructor;
import org.project.moodbotbackend.dto.app.AppResponse;
import org.project.moodbotbackend.repository.ChatRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppService {

    private final ChatRepository chatRepository;

    public AppResponse generateMainPage() {

        // obtain username from securityContextHolder
        // peruse the chat repository for 'chats'

        // build a response to return using the builder notation
        return new AppResponse(null);
    }
}
