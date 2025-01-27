package com.morpheus.stockbetting.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

public class HttpMethodValidationFilter extends OncePerRequestFilter {

    private static final Set<String> ALLOWED_METHODS = Set.of(
        HttpMethod.GET.name(),
        HttpMethod.POST.name(),
        HttpMethod.PUT.name(),
        HttpMethod.DELETE.name(),
        HttpMethod.OPTIONS.name(),
        HttpMethod.HEAD.name()
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();

        if (!ALLOWED_METHODS.contains(method)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "HTTP method " + method + " is not allowed");
            return;
        }

        // If the method is valid, continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
