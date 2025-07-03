package org.project.moodbotbackend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.project.moodbotbackend.util.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenService tokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String AUTH_PREFIX = "Bearer ";

        if (authHeader != null && authHeader.startsWith(AUTH_PREFIX)) {
            String token = authHeader.substring(AUTH_PREFIX.length());
            if (tokenService.isTokenAllowed(token)) {
                tokenService.disallowToken(token);
            } else {
                throw new RuntimeException("token is disallowed!");     // customize exception later.
            }
            // invalidate refresh token
        } else {
            throw new RuntimeException("failed to logout!");
        }

        // invalidate session
        HttpSession session = request.getSession(false);
        if (session != null)
            session.invalidate();

        // clear security context
        SecurityContextHolder.clearContext();

    }
}
