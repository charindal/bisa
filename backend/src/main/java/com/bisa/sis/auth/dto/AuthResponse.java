package com.bisa.sis.auth.dto;

import com.bisa.sis.user.Role;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        long expiresInSeconds,
        UserSummary user
) {
    public record UserSummary(
            UUID id,
            String email,
            String firstName,
            String lastName,
            Role role
    ) {
    }
}
