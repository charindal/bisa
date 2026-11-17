package com.bisa.sis.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Issues and validates short-lived JWT access tokens. Refresh tokens are handled
 * separately (opaque, DB-backed) in {@link com.bisa.sis.auth.RefreshTokenService}.
 */
@Service
public class JwtService {

    private final JwtProperties properties;
    private SecretKey signingKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void init() {
        String secret = properties.getSecret();
        boolean isProdSecretMissing = secret == null || secret.isBlank();
        boolean isTooShort = secret != null && secret.getBytes(StandardCharsets.UTF_8).length < 32;
        if (isProdSecretMissing || isTooShort) {
            throw new IllegalStateException(
                    "app.jwt.secret must be set and at least 32 bytes long. " +
                    "Set the JWT_SECRET environment variable.");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(SecurityUser user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(properties.getAccessTokenTtlMinutes() * 60L);
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getUsername())
                .claim("role", user.getUser().getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    public long getAccessTokenTtlSeconds() {
        return properties.getAccessTokenTtlMinutes() * 60L;
    }

    /**
     * @throws JwtException if the token is malformed, expired, or has an invalid signature.
     */
    public Claims parseAndValidate(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID getUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }
}
