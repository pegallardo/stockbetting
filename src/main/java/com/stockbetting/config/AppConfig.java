package com.stockbetting.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.stockbetting.middleware.ErrorHandlingMiddleware;
import com.stockbetting.middleware.HttpMethodValidationMiddleware;
import com.stockbetting.middleware.RequestLoggingMiddleware;

@Configuration
public class AppConfig {

    @Bean
    public FilterRegistrationBean<ErrorHandlingMiddleware> errorHandlingFilter(
            ErrorHandlingMiddleware errorHandlingMiddleware) {
        var registrationBean = new FilterRegistrationBean<ErrorHandlingMiddleware>();
        registrationBean.setFilter(errorHandlingMiddleware);
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RequestLoggingMiddleware> requestLoggingFilter(
            RequestLoggingMiddleware requestLoggingMiddleware) {
        var registrationBean = new FilterRegistrationBean<RequestLoggingMiddleware>();
        registrationBean.setFilter(requestLoggingMiddleware);
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<HttpMethodValidationMiddleware> methodValidationFilter(
            HttpMethodValidationMiddleware methodValidationMiddleware) {
        var registrationBean = new FilterRegistrationBean<HttpMethodValidationMiddleware>();
        registrationBean.setFilter(methodValidationMiddleware);
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        return registrationBean;
    }
}
