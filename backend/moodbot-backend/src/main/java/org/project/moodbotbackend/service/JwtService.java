package org.project.moodbotbackend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.project.moodbotbackend.entity.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    final String secret;

    @Value("${security.jwt.expiration}")
    long expiration;    // six hours

    @Value("${security.jwt.refresh.expiration}")
    long refreshExpiration;     // three days

    public JwtService() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGenerator.generateKey();
            secret = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private SecretKey generateKey() {
        byte[] secretByte = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(secretByte);
    }

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();

        try {

            return Jwts.builder()
                    .subject(email)
                    .claims(claims)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(generateKey())
                    .compact();
        } catch (JwtException e) {
            throw new RuntimeException("failed to generate access token.");
        }

    }

    private Claims extractClaims(String token) {
        try {

            return Jwts.parser()
                    .verifyWith(generateKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("this is an invalid token");
        }
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean verifyToken(String token, UserPrincipal userPrincipal) {
        return (extractEmail(token).equals(userPrincipal.getEmail()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }


}
