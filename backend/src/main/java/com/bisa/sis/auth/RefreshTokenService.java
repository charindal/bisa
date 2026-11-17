package com.bisa.sis.auth;

import com.bisa.sis.security.JwtProperties;
import com.bisa.sis.user.User;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

/**
 * Refresh tokens are opaque random strings, not JWTs: the raw value is only ever
 * handed to the browser (as an httpOnly cookie), while the DB stores a SHA-256 hash.
 * This makes them revocable and rotated on every use, unlike the short-lived JWT access token.
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(RefreshTokenRepository repository, JwtProperties jwtProperties) {
        this.repository = repository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public String issue(User user) {
        String rawToken = generateRawToken();
        Instant expiresAt = Instant.now().plusSeconds(jwtProperties.getRefreshTokenTtlDays() * 24L * 60 * 60);
        repository.save(new RefreshToken(user, hash(rawToken), expiresAt));
        return rawToken;
    }

    /**
     * Validates the presented raw token, revokes it, and issues a replacement for the same user.
     *
     * @throws InvalidRefreshTokenException if the token is unknown, expired, or already revoked
     */
    @Transactional
    public RotatedToken rotate(String rawToken) {
        RefreshToken existing = repository.findByTokenHash(hash(rawToken))
                .orElseThrow(InvalidRefreshTokenException::new);

        if (!existing.isValid()) {
            throw new InvalidRefreshTokenException();
        }

        existing.revoke();
        String newRawToken = issue(existing.getUser());
        return new RotatedToken(existing.getUser(), newRawToken);
    }

    @Transactional
    public void revoke(String rawToken) {
        repository.findByTokenHash(hash(rawToken)).ifPresent(RefreshToken::revoke);
    }

    public long getTtlSeconds() {
        return jwtProperties.getRefreshTokenTtlDays() * 24L * 60 * 60;
    }

    private String generateRawToken() {
        byte[] bytes = KeyGenerators.secureRandom(32).generateKey();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public record RotatedToken(User user, String rawToken) {
    }

    public static class InvalidRefreshTokenException extends RuntimeException {
        public InvalidRefreshTokenException() {
            super("Refresh token is invalid, expired, or revoked");
        }
    }
}
