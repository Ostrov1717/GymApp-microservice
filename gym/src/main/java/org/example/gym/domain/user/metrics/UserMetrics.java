package org.example.gym.domain.user.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMetrics {
    private final MeterRegistry meterRegistry;

    public void incrementAuthenticationSuccess(String username) {
        Counter.builder("Authentication successful")
                .tag("status", "success")
                .tag("username", username)
                .register(meterRegistry)
                .increment();
    }
    public void incrementAuthenticationFailed(String username) {
        Counter.builder("Authentication failed")
                .tag("status", "failed")
                .tag("username", username)
                .register(meterRegistry)
                .increment();
    }
}
