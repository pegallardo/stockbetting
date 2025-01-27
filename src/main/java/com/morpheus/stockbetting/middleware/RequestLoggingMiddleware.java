package com.morpheus.stockbetting.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.morpheus.stockbetting.util.HeadersUtil;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(2)
public class RequestLoggingMiddleware extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingMiddleware.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        long startTime = System.currentTimeMillis();

        // Wrap the request and response to capture their content
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // Add tracking header to response
        response.setHeader("X-Request-ID", requestId);

        try {
            logIncomingRequest(requestWrapper, requestId);
            filterChain.doFilter(requestWrapper, responseWrapper);
            logOutgoingResponse(responseWrapper, requestId, System.currentTimeMillis() - startTime);
        } finally {
            // Ensure response body is copied to the original response
            responseWrapper.copyBodyToResponse();
            MDC.clear(); // Clear MDC to avoid memory leaks
        }
    }

    private void logIncomingRequest(ContentCachingRequestWrapper request, String requestId) {
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

    private void logOutgoingResponse(ContentCachingResponseWrapper response, String requestId, long duration) {
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
