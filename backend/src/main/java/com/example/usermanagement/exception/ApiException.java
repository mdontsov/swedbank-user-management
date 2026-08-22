package com.example.usermanagement.exception;

import java.time.Instant;
import java.util.Map;

public record ApiException(
        Instant timestamp,
        int status,
        String error,
        String path,
        Map<String, String> fieldErrors
) {
}

