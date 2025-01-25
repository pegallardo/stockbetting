package com.betting.stockbetting.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record ErrorResponse(
    String path,
    int status,
    String error,
    String message,
    List<String> details,
    Instant timestamp,
    String traceId
) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String path;
        private int status;
        private String error;
        private String message;
        private List<String> details = new ArrayList<>();
        private Instant timestamp;
        private String traceId;

        public Builder path(String path) { this.path = path; return this; }
        public Builder status(int status) { this.status = status; return this; }
        public Builder error(String error) { this.error = error; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder details(List<String> details) { this.details = details; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }
        public Builder traceId(String traceId) { this.traceId = traceId; return this; }

        public ErrorResponse build() {
            return new ErrorResponse(path, status, error, message, details, timestamp, traceId);
        }
    }
}
