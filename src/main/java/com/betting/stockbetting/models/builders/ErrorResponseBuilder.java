
package com.betting.stockbetting.models.builders;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseBuilder(
    String path,
    int status,
    String error,
    String message,
    List<String> details,
    LocalDateTime timestamp,
    String traceId
) {
    public static ErrorResponseBuilder create() {
        return new ErrorResponseBuilder(
            null,
            500,
            null,
            null,
            List.of(),
            LocalDateTime.now(),
            null
        );
    }

    public ErrorResponseBuilder withPath(String path) {
        return new ErrorResponseBuilder(path, status, error, message, details, timestamp, traceId);
    }

    public ErrorResponseBuilder withStatus(int status) {
        return new ErrorResponseBuilder(path, status, error, message, details, timestamp, traceId);
    }

    public ErrorResponseBuilder withError(String error) {
        return new ErrorResponseBuilder(path, status, error, message, details, timestamp, traceId);
    }

    public ErrorResponseBuilder withMessage(String message) {
        return new ErrorResponseBuilder(path, status, error, message, details, timestamp, traceId);
    }

    public ErrorResponseBuilder withDetails(List<String> details) {
        return new ErrorResponseBuilder(path, status, error, message, details, timestamp, traceId);
    }

    public ErrorResponseBuilder withTraceId(String traceId) {
        return new ErrorResponseBuilder(path, status, error, message, details, timestamp, traceId);
    }
}
