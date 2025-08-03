package org.project.moodbotbackend.websocket;

import java.security.Principal;
import java.util.Collections;

import org.project.moodbotbackend.entity.User;
import org.project.moodbotbackend.entity.UserPrincipal;
import org.project.moodbotbackend.exceptions.auth.AuthException;
import org.project.moodbotbackend.repository.AuthRepository;
import org.project.moodbotbackend.service.JwtService;
import org.project.moodbotbackend.service.MyUserDetailsService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtService jwtService;
    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        // check if 'message' is CONNECT
        if (headerAccessor.getCommand() != null && headerAccessor.getCommand().equals(StompCommand.CONNECT)) {
            String authHeader = headerAccessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring("Bearer ".length());

                // token validation
                User user = validateAndGetUser(token);

                if (user != null) {

                    UserPrincipal userPrincipal = (UserPrincipal) myUserDetailsService.loadUserByUsername(user.getEmail());

                    headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                    Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                    if (headerAccessor != null) {
                        headerAccessor.setUser(authentication);
                    } else {
                        throw new AuthException(401, "we don't know you");
                    }

                    // set authenticated user in spring security context
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                }
            }
        }

        return message;
    }

    User validateAndGetUser(String token) {
        String email = jwtService.extractEmail(token);
        UserPrincipal userPrincipal = (UserPrincipal) myUserDetailsService.loadUserByUsername(email);

        // load username into Principal for use in controller and service classes
        if (userPrincipal != null && jwtService.verifyToken(token, userPrincipal)) {
            return authRepository.findUserByEmail(email);
        }

        throw new AuthException(400, "invalid token");
    }
}
