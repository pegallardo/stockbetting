
package com.betting.stockbetting.middlewares;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

/**
 * Middleware for request/response logging and tracking
 */
@Component
public class RequestLoggingMiddleware extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingMiddleware.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // Add tracking headers
        response.setHeader("X-Request-ID", requestId);
        
        try {
            logRequest(requestWrapper, requestId);
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            logResponse(responseWrapper, requestId, System.currentTimeMillis() - startTime);
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, String requestId) {
        logger.info("""
            Incoming Request:
            ID: {}
            Method: {}
            URI: {}
            Headers: {}
            """,
            requestId,
            request.getMethod(),
            request.getRequestURI(),
            HeadersUtil.getHeadersMap(request)
        );
    }

    private void logResponse(ContentCachingResponseWrapper response, String requestId, long duration) {
        logger.info("""
            Outgoing Response:
            ID: {}
            Status: {}
            Duration: {} ms
            Headers: {}
            """,
            requestId,
            response.getStatus(),
            duration,
            HeadersUtil.getHeadersMap(response)
        );
    }
}
