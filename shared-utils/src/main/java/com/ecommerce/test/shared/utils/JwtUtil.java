package com.ecommerce.test.shared.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.function.Function;

public class JwtUtil {
    private final String secret;
    private final Duration duration;

    public JwtUtil(String secret, Duration duration) {
        this.secret = secret;
        this.duration = duration;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Long userId, String userEmail) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + duration.toMillis());
        return Jwts.builder()
                .id(userId.toString())
                .subject(userEmail)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractId(String token) {
        try {
            return Long.parseLong(extractClaim(token, Claims::getId));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
