package com.betting.stockbetting.exceptions;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized API error response structure
 */
public record ApiError(
    String path,
    String message,
    int statusCode,
    LocalDateTime timestamp,
    List<String> details
) {}
