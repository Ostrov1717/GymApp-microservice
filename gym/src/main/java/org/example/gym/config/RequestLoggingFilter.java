package org.example.gym.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@WebFilter(urlPatterns = "/*")
@Slf4j
@RequiredArgsConstructor
public class RequestLoggingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String transactionId = java.util.UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);
        log.info("Incoming request: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        try {
            chain.doFilter(request, response);
        } finally {
            log.info("Response for request with status: {}", httpResponse.getStatus());
            MDC.remove("transactionId");
        }
    }

    @Override
    public void destroy() {
    }
}
