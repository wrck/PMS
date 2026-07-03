package com.dp.plat.system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * JWT token generation and validation using jjwt 0.12.x.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    /**
     * Generate a JWT token for the given user.
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Get the user id from the token.
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        try {
            return Long.valueOf(claims.getSubject());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Get the username from the token.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * Get the JWT id (jti) from the token. Used for token blacklisting on logout.
     */
    public String getJtiFromToken(String token) {
        try {
            return parseClaims(token).getId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the token expiration timestamp (ms since epoch).
     */
    public long getExpirationFromToken(String token) {
        Date exp = parseClaims(token).getExpiration();
        return exp != null ? exp.getTime() : 0L;
    }

    /**
     * Get the configured token expiration (ms).
     */
    public long getExpiration() {
        return expiration;
    }

    /**
     * Validate the token signature and expiration.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
