package org.example.gym.domain.user.metrics;

import lombok.AllArgsConstructor;
import org.example.gym.domain.user.service.UserService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class ActiveUsersHealthIndicator implements HealthIndicator {
    private final UserService userService;

    @Override
    public Health health() {
        long activeUsers = userService.getActiveUserCount();
        Health.Builder status = Health.up();
        if (activeUsers > 0) {
            Map<String, Object> details = new HashMap<>();
            details.put("Active Users", activeUsers);
            details.put("Message", "System is operational");
            return status
                    .withDetails(details)
                    .build();
        } else {
            return Health.down()
                    .withDetail("Active Users", activeUsers)
                    .withDetail("Message", "No active users, potential issue!")
                    .build();
        }
    }
}
