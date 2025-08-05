package org.project.moodbotbackend.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Component
public class CustomCorsConfiguration implements CorsConfigurationSource {
    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

        String origin = request.getHeader("Origin");

        CorsConfiguration config = new CorsConfiguration();

        if (origin != null) {
            config.setAllowedOrigins(Collections.singletonList(origin));
            System.out.println(origin);
        }
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowCredentials(true);

        return config;
    }
}
