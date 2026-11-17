package com.bisa.sis.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Joins the user eagerly: callers (e.g. token rotation) need to read fields off the user
     * after this method's transaction closes, which a lazy proxy can't support (open-in-view
     * is disabled).
     */
    @Query("select rt from RefreshToken rt join fetch rt.user where rt.tokenHash = :tokenHash")
    Optional<RefreshToken> findByTokenHashWithUser(String tokenHash);
}
