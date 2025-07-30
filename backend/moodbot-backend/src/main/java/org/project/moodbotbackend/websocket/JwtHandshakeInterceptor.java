package org.project.moodbotbackend.websocket;

import java.util.Map;

import org.project.moodbotbackend.entity.UserPrincipal;
import org.project.moodbotbackend.repository.AuthRepository;
import org.project.moodbotbackend.service.ChatService;
import org.project.moodbotbackend.service.JwtService;
import org.project.moodbotbackend.service.MyUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;
    private final AuthRepository authRepository;
    private final ChatService chatService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest req = servletRequest.getServletRequest();
//            String authHeader = req.getHeader("Authorization");
            String token = req.getParameter("token");     // read from query params
            System.out.println(token);

            // getting principal from token.
            if (token != null) {
                String email = jwtService.extractEmail(token);
                UserPrincipal userPrincipal = (UserPrincipal) myUserDetailsService.loadUserByUsername(email);

                // load username into Principal for use in controller and service classes
                if (userPrincipal != null && jwtService.verifyToken(token, userPrincipal)) {
                    /*String username = userPrincipal.getUsername();
                    attributes.put("username", username);*/
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            userPrincipal, null, userPrincipal.getAuthorities()
                    );
                    attributes.put("user", auth);
                    return true;
                }
            } response.setStatusCode(HttpStatus.BAD_REQUEST);

        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
       /*  // create new chat instance
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();
        String email = ((UserPrincipal) myUserDetailsService.loadUserByUsername(identifier)).getEmail();

        User user = authRepository.findUserByEmail(email);

        Chat chat = Chat.builder()
                .user(user)
                .build();

        // cache empty chat.
        if (chatService.newChats.getIfPresent(user.getUsername()) != null) {
            chatService.newChats.invalidate(user.getUsername());    // delete any existing chats in cache
        }
        chatService.newChats.put(user.getUsername(), chat); */

    }
}
