package com.betting.stockbetting.middlewares;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityMiddleware extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        response.setHeader("X-Content-Type-Options", "nosniff"); // Prevents MIME-type sniffing
        response.setHeader("X-Frame-Options", "DENY"); // Prevents clickjacking
        response.setHeader("X-XSS-Protection", "1; mode=block"); // XSS protection
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains"); // HSTS
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin"); // Controls referrer information
        response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()"); // Restricts features
        response.setHeader("Cache-Control", "no-store, max-age=0");
        
        filterChain.doFilter(request, response);
    }
}
