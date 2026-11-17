package com.bisa.sis.user.dto;

import com.bisa.sis.user.Role;
import com.bisa.sis.user.User;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        Role role,
        boolean enabled,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getRole(), user.isEnabled(), user.getCreatedAt());
    }
}
