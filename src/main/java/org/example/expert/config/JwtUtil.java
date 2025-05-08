package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    private final String secretKey = "and0X3N1cGVyX3NlY3VyZV9rZXlfMjAyNV8kM2NVcjNAlndU";
    private static final long EXPIRATION_TIME = 60 * 60 * 1000L; // 60분

    public String generateToken(Long userId, String email, UserRole userRole, String nickname) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .claim("UserRole", userRole)
                .claim("nickname", nickname)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            return null;
        }
    }
}
