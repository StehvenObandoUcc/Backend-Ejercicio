package com.example.decoratorapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import java.io.IOException;

@Component
public class ApiKeyFilter implements Filter {

    private final String apiKey;

    public ApiKeyFilter(@Value("${app.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String incoming = httpRequest.getHeader("X-API-KEY");
        if (incoming == null || !incoming.equals(apiKey)) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setContentType("application/json");
            String body = "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Invalid or missing API key\"}";
            httpResponse.getWriter().write(body);
            return;
        }

        chain.doFilter(request, response);
    }
}
