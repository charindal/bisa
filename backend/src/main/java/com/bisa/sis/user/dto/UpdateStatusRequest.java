package com.bisa.sis.user.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(@NotNull Boolean enabled) {
}
