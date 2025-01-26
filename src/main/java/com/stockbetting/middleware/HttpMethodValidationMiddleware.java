package com.stockbetting.middleware;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class HttpMethodValidationMiddleware extends OncePerRequestFilter {

    private static final Set<String> ALLOWED_METHODS = Set.of(
        HttpMethod.GET.name(),
        HttpMethod.POST.name(),
        HttpMethod.PUT.name(),
        HttpMethod.DELETE.name(),
        HttpMethod.OPTIONS.name(),
        HttpMethod.HEAD.name()
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        
        String method = request.getMethod();
        
        if (!ALLOWED_METHODS.contains(method)) {
            response.sendError(
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "HTTP method " + method + " is not allowed"
            );
            return;
        }

        validateMethodSpecificRequirements(request, response);
        
        if (!response.isCommitted()) {
            filterChain.doFilter(request, response);
        }
    }

    private void validateMethodSpecificRequirements(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        
            String method = request.getMethod();
            switch (method) {
                case "POST":
                    validatePostRequest(request, response);
                    break;
                case "PUT":
                    validatePutRequest(request, response);
                    break;
                case "DELETE":
                    validateDeleteRequest(request, response);
                    break;
                case "GET":
                    validateGetRequest(request, response);
                    break;
                case "HEAD":
                    validateHeadRequest(request, response);
                    break;
                default:
                    // OPTIONS allowed by default
                    break;
            }
    }

    private void validatePostRequest(HttpServletRequest request, 
                                   HttpServletResponse response) throws IOException {
        if (!hasValidContentType(request)) {
            response.sendError(
                HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                "Content-Type must be application/json"
            );
        }
    }

    private void validatePutRequest(HttpServletRequest request, 
                                  HttpServletResponse response) throws IOException {
        if (!hasValidContentType(request) || !hasRequiredId(request)) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid PUT request"
            );
        }
    }

    private void validateDeleteRequest(HttpServletRequest request, 
                                     HttpServletResponse response) throws IOException {
        if (!hasRequiredId(request)) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Resource ID is required for DELETE"
            );
        }
    }

    private void validateGetRequest(HttpServletRequest request, 
                                  HttpServletResponse response) {
        // GET requests are valid by default
    }

    private void validateHeadRequest(HttpServletRequest request, 
                                   HttpServletResponse response) {
        // HEAD requests follow GET validation
    }

    private boolean hasValidContentType(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && 
               contentType.toLowerCase().contains("application/json");
    }

    private boolean hasRequiredId(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.matches(".*/\\d+$") || 
               request.getParameter("id") != null;
    }
}
