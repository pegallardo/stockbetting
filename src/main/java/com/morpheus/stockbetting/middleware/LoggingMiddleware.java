
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

import java.io.IOException;
import java.util.UUID;

@Component
@Order(2)
public class LoggingMiddleware extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingMiddleware.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        long startTime = System.currentTimeMillis();

        try {
            logger.info("Request: {} {} from {}", 
                request.getMethod(), 
                request.getRequestURI(), 
                request.getRemoteAddr()
            );
            
            filterChain.doFilter(request, response);
            
            logger.info("Response: {} completed in {}ms", 
                response.getStatus(), 
                System.currentTimeMillis() - startTime
            );
        } finally {
            MDC.clear();
        }
    }
}
