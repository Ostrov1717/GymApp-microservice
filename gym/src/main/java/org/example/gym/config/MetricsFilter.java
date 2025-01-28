package org.example.gym.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@WebFilter(urlPatterns = "/*")
@Slf4j
public class MetricsFilter implements Filter {

    private final MeterRegistry meterRegistry;
    private final Timer requestTimer;
    private final Counter requestCounter;

    public MetricsFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.requestTimer = Timer.builder("HTTP request timer")
                .description("Measures the time taken to process HTTP requests")
                .tags("type", "request")
                .register(meterRegistry);
        this.requestCounter = Counter.builder("HTTP request count")
                .description("Counts the number of HTTP requests")
                .tags("type", "request")
                .register(meterRegistry);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        if (httpRequest.getRequestURI().startsWith("/actuator/prometheus")) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }
        Timer.Sample sample = Timer.start(meterRegistry);
        requestCounter.increment();
        try {
            chain.doFilter(servletRequest, servletResponse);
        } finally {
            sample.stop(requestTimer);
//            log.info("Request processing time: {} ms", meterRegistry.get("HTTP request timer").timer().mean(TimeUnit.MILLISECONDS));
        }
    }

    @Override
    public void destroy() {
    }
}
