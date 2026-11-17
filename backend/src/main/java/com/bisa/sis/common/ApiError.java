package com.bisa.sis.common;

import java.time.Instant;
import java.util.List;

public record ApiError(int status, String message, List<String> details, Instant timestamp) {

    public ApiError(int status, String message) {
        this(status, message, List.of(), Instant.now());
    }

    public ApiError(int status, String message, List<String> details) {
        this(status, message, details, Instant.now());
    }
}
