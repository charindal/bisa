package com.bisa.sis.user.dto;

import com.bisa.sis.user.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(@NotNull Role role) {
}
