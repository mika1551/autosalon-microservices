package infrastructure.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int value, String error,
        String message,
        String path
){}


