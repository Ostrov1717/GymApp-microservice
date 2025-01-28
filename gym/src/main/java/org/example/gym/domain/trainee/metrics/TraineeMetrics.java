package org.example.gym.domain.trainee.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TraineeMetrics {
    private final Counter newTraineeRegistration;

    TraineeMetrics(MeterRegistry registry) {
        this.newTraineeRegistration = registry.counter("New trainee count");
    }

    public void incrementNewTrainee() {
        newTraineeRegistration.increment();
    }
}
