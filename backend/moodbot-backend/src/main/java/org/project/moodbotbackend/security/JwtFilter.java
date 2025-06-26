package org.project.moodbotbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.project.moodbotbackend.util.TokenService;
import org.project.moodbotbackend.entity.UserPrincipal;
import org.project.moodbotbackend.service.JwtService;
import org.project.moodbotbackend.service.MyUserDetailsService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtService jwtService;
    private final List<String> PUBLIC_URLS = List.of(
            "/api/v1/moodbot/auth/register",
            "/api/v1/moodbot/auth/login",
            "/api/v1/moodbot/auth/verify"
    );
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        final String AUTH_PREFIX = "Bearer ";
        String token = null;
        String email = null;
        String url = request.getRequestURI();

        if (PUBLIC_URLS.contains(url)){
            System.out.println("skipping jwt filter for path: " + url);
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader != null && authHeader.startsWith(AUTH_PREFIX)) {
            token = authHeader.substring(AUTH_PREFIX.length());
            email = jwtService.extractEmail(token);
        }

        // check if token has been disallowed
        if (!tokenService.isTokenAllowed(token)) {
            System.out.println("blacklisting token!");
            throw new BadCredentialsException("expired or disallowed token!");  // handle this better later on.
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserPrincipal userPrincipal = (UserPrincipal) myUserDetailsService.loadUserByUsername(email);

            if (jwtService.verifyToken(token, userPrincipal)) {
                UsernamePasswordAuthenticationToken authToken
                        = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } else {
            if (token != null)
                throw new BadCredentialsException("invalid token!");    // change to JwtException or custom later.
            else
                throw new BadRequestException("problematic request!");    // problem with the request
        }

        filterChain.doFilter(request, response);
    }
}
