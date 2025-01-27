package com.morpheus.stockbetting.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.morpheus.stockbetting.middleware.ErrorHandlingMiddleware;
import com.morpheus.stockbetting.middleware.RequestLoggingMiddleware;

@Configuration
public class AppConfig {

    /**
     * Registers the error handling middleware as a filter that handles all "/api/*" endpoints.
     * This middleware catches all exceptions and errors, and returns a standardized API error response.
     * The order of this filter is set to the highest precedence, so it will catch any exceptions that occur
     * in other filters before they reach the application's endpoints.
     * @param errorHandlingMiddleware the error handling middleware to register
     * @return a filter registration bean for the error handling middleware
     */
    @Bean
    public FilterRegistrationBean<ErrorHandlingMiddleware> errorHandlingFilter(
            ErrorHandlingMiddleware errorHandlingMiddleware) {
        var registrationBean = new FilterRegistrationBean<ErrorHandlingMiddleware>();
        registrationBean.setFilter(errorHandlingMiddleware);
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    /**
     * Registers the request logging middleware as a filter that logs all "/api/*" endpoint requests and responses.
     * This middleware logs the request and response bodies, headers, and status codes, and the time taken to process the request.
     * The order of this filter is set to the highest precedence, so it will catch any exceptions that occur
     * in other filters before they reach the application's endpoints.
     * @param requestLoggingMiddleware the request logging middleware to register
     * @return a filter registration bean for the request logging middleware
     */
    @Bean
    public FilterRegistrationBean<RequestLoggingMiddleware> requestLoggingFilter(
            RequestLoggingMiddleware requestLoggingMiddleware) {
        var registrationBean = new FilterRegistrationBean<RequestLoggingMiddleware>();
        registrationBean.setFilter(requestLoggingMiddleware);
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registrationBean;
    }

}
