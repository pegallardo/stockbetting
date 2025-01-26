package com.stockbetting.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling HTTP headers
 */
public final class HeadersUtil {
    private HeadersUtil() {}

    public static Map<String, String> getHeadersMap(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Collections.list(request.getHeaderNames())
                .forEach(headerName -> 
                    headers.put(headerName, request.getHeader(headerName)));
        return headers;
    }

    public static Map<String, String> getHeadersMap(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        response.getHeaderNames()
                .forEach(headerName -> 
                    headers.put(headerName, response.getHeader(headerName)));
        return headers;
    }
}
