
package com.morpheus.stockbetting.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morpheus.stockbetting.dto.response.ErrorResponse;
import com.morpheus.stockbetting.exception.StockNotFoundException;
import com.morpheus.stockbetting.exception.ValidationException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Order(1)
public class ErrorHandlingMiddleware extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingMiddleware.class);
    private final ObjectMapper objectMapper;

    public ErrorHandlingMiddleware(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws IOException {

        var requestWrapper = new ContentCachingRequestWrapper(request);
        var responseWrapper = new ContentCachingResponseWrapper(response);
        var errorRef = new AtomicReference<Throwable>();

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
            handlePossibleError(requestWrapper, responseWrapper);
        } catch (Exception e) {
            errorRef.set(e);
            handleException(requestWrapper, responseWrapper, e);
        } finally {
            responseWrapper.copyBodyToResponse();
            logError(errorRef.get(), requestWrapper);
        }
    }

    private void handlePossibleError(ContentCachingRequestWrapper request, 
                                   ContentCachingResponseWrapper response) throws IOException {
        if (response.getStatus() >= 400) {
            var errorResponse = ErrorResponse.builder()
                .path(request.getRequestURI())
                .status(response.getStatus())
                .timestamp(Instant.now())
                .traceId(request.getHeader("X-Trace-ID"))
                .error(HttpStatus.valueOf(response.getStatus()).getReasonPhrase())
                .build();

            writeErrorResponse(response, errorResponse);
        }
    }

    private void handleException(ContentCachingRequestWrapper request,
                               ContentCachingResponseWrapper response,
                               Exception ex) throws IOException {
        var errorResponse = switch (ex) {
            case StockNotFoundException e -> ErrorResponse.builder()
                .path(request.getRequestURI())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Stock Not Found")
                .message(e.getMessage())
                .timestamp(Instant.now())
                .traceId(request.getHeader("X-Trace-ID"))
                .build();
                
            case ValidationException e -> ErrorResponse.builder()
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message(e.getMessage())
                .timestamp(Instant.now())
                .traceId(request.getHeader("X-Trace-ID"))
                .build();
                
            default -> ErrorResponse.builder()
                .path(request.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .traceId(request.getHeader("X-Trace-ID"))
                .build();
        };

        writeErrorResponse(response, errorResponse);
    }

    private void writeErrorResponse(HttpServletResponse response, 
                                  ErrorResponse errorResponse) throws IOException {
        response.setStatus(errorResponse.status());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(objectMapper.writeValueAsBytes(errorResponse));
    }

    private void logError(Throwable error, HttpServletRequest request) {
        if (error != null) {
            logger.error("Request {} {} failed: {}", 
                request.getMethod(), 
                request.getRequestURI(), 
                error.getMessage(), 
                error);
        }
    }
}
