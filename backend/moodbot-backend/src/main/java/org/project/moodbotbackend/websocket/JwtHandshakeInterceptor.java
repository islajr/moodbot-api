package org.project.moodbotbackend.websocket;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.project.moodbotbackend.entity.UserPrincipal;
import org.project.moodbotbackend.exceptions.auth.AuthException;
import org.project.moodbotbackend.service.JwtService;
import org.project.moodbotbackend.service.MyUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@AllArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest req = servletRequest.getServletRequest();
            String authHeader = req.getHeader("Authorization");
            String token = authHeader.substring(7);     // 7 being the length of "Bearer "

            // getting principal from token.
            String email = jwtService.extractEmail(token);

            UserPrincipal userPrincipal = (UserPrincipal) myUserDetailsService.loadUserByUsername(email);

            if (userPrincipal != null && jwtService.verifyToken(token, userPrincipal)) {
                String username = userPrincipal.getUsername();
                attributes.put("username", username);
                return true;
            }
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
