package org.project.moodbotbackend.websocket;

import lombok.RequiredArgsConstructor;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtService jwtService;
    private final AuthRepository authRepository;

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
                    // set authenticated user in spring security context
                    SecurityContextHolder.getContext().setAuthentication(new Authentication() {
                        @Override
                        public Collection<? extends GrantedAuthority> getAuthorities() {
                            return Collections.singleton(new SimpleGrantedAuthority("USER"));
                        }

                        @Override
                        public Object getCredentials() {
                            return user.getPassword();
                        }

                        @Override
                        public Object getDetails() {
                            return myUserDetailsService.loadUserByUsername(user.getUsername()); // could be null
                        }

                        @Override
                        public Object getPrincipal() {
                            return myUserDetailsService.loadUserByUsername(user.getUsername());
                        }

                        @Override
                        public boolean isAuthenticated() {
                            return true;
                        }

                        @Override
                        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

                        }

                        @Override
                        public String getName() {
                            return user.getUsername();
                        }
                    });
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
